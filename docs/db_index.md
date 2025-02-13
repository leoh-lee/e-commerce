# E-commerce 시나리오 인덱스 적용

## 1. 개요
이번 주차에서는 E-commerce 시나리오에 DB 인덱스를 적용하고, 성능 개선 정도를 비교하는 작업을 합니다.

앞서 우리는 이전 주차에 웹 애플리케이션에서 조회 성능을 향상시키기 위해 레디스를 활용한 캐싱을 학습하였습니다. 캐싱은 DB로부터 여러번 조회해야할 것을 한 번으로 줄여줌으로써 DB에 부하를 줄이는 역할을 했습니다. 하지만 다음과 같은 경우도 있습니다.

- 한 번의 조회가 너무 복잡하고 오래 걸림
- 조회가 너무 빈번함
- 캐시하기 어려움

이럴 때에는 DB 인덱스를 손 봐줘야 합니다.
> 💡**DB 인덱스란?**
>
> DB 인덱스는 특정 자료구조(예: B-Tree, Hash Table 등)를 활용하여 데이터베이스의 검색 성능을 향상시키는 기법입니다. 인덱스를 생성하면 특정 컬럼의 데이터를 빠르게 조회할 수 있지만, 인덱스를 유지·관리하는 데 추가적인 저장 공간과 쓰기 비용이 발생합니다.
>
> 참조: Chat GPT

우선 E-commerce에서 DB 인덱스가 필요한 시나리오를 파악해보겠습니다. 

## 2. 인덱스가 필요한 시나리오
많은 조회 시나리오 중 인덱스가 필요하다고 판단되는 시나리오를 다음과 같이 추려봤습니다. 각 시나리오 별로 판단근거를 알아보겠습니다.

### 2.1. 사용자 쿠폰 조회
쿠폰 발급(수정)보다는 조회가 많을 것으로 예상되어 선택하였습니다.

사용자 쿠폰 테이블(user_coupon)은 사용자가 발급받은 쿠폰 데이터가 담긴 테이블입니다. 사용자 ID와 쿠폰 발급 상태(발급, 사용, 만료 등)를 조건으로 하여 검색할 수 있기 때문에 인덱스 적용 시 성능 개선이 될 것이라 판단하였습니다.

### 2.2. 상위 주문 상품 조회
상위 주문 상품 조회는 E-commerce에서 스케줄러를 통해 하루에 한 번만 수행합니다. 하지만 이 한 번의 작업이 복잡한 조회이기 때문에 선택하였습니다.

해당 조회 쿼리는 `WHERE`, `GROUP BY`, `ORDER_BY`를 모두 사용하기 때문에 유의미한 인덱싱 결과가 나올 것이라 판단하였습니다.

## 3. 시나리오별 인덱스 적용
### 3.1. 사용자 쿠폰 조회
#### 3.1.1. 조회 쿼리
```sql
select  
    id,  
    coupon_id,  
    created_at,  
    expired_date,  
    updated_at,  
    use_date,  
    user_coupon_status,  
    user_id  
from  
    user_coupon  
where  
    user_id = 511  
    and user_coupon_status = 'ISSUED';
```
#### 3.1.2. 카디널리티 분석
카디널리티는 `user_id`가 `user_coupon_status`보다 높다고 추정됩니다.

아래는 `count` 쿼리를 통해 더미데이터에 대한 카디널리티를 분석한 결과입니다.
- `count` 쿼리
```sql
select 
    count(distinct(user_id)), 
    count(distinct(user_coupon_status)) 
from user_coupon;
```
- 결과

| count\(distinct\(user\_id\)\) | count\(distinct\(user\_coupon\_status\)\) |
| :--- | :--- |
| 374238 | 4 |


`user_coupon` 테이블의 데이터 700,000개를 기준으로 `user_id`는 중복없이 374,238개 있고, `user_coupon_status`는 4개 있습니다. 이미 예상하신 분도 계시겠지만, `user_coupon_status`는 `enum`타입이라, 4 종류밖에 없습니다. 그래서 중복이 상당히 많을 수 밖에 없습니다.

#### 3.1.3. 적용할 인덱스 선정
컬럼이 `user_id`와 `user_coupon_status` 두 개이기 때문에 복합인덱스를 생성해주어야 합니다. 일반적으로 인덱스는 카디널리티가 높은 순서로 선언해주는 것이 좋습니다.

- `user_id`
  - 약 36만 개의 고유 값을 가지므로 카디널리티가 매우 높습니다.
  - 특정 user_id를 조건으로 조회할 경우, 결과 집합을 신속하게 좁힐 수 있습니다.
- `user_coupon_status`
  - enum 타입으로 4가지 값만 가지므로 카디널리티가 낮습니다.
  - 단독 조건으로 조회할 경우, 상대적으로 많은 데이터가 선택될 가능성이 높습니다.

따라서, 복합 인덱스는 (user_id, user_coupon_status) 순서로 생성하는 것이 바람직합니다.

> 카디널리티가 높은 컬럼이 먼저 온다면, 첫 번째 인덱스를 탐색할 때에는 조건에 맞는 row를 빠르게 찾을 수 있습니다.
> 
> 반대로 첫 번째가 카디널리티가 낮은 컬럼인 경우에는 두 번째 조건을 적용하기 위해 상대적으로 많은 row를 스캔해야 합니다.

### 3.2. 상위 주문 상품 조회
#### 3.2.1. 조회 쿼리
```sql
    select
        product_id,
        sum(quantity) 
    from
        order_product
    where
        created_at > '2025-02-09'
    group by
        product_id 
    order by
        sum(quantity) desc 
    limit
        3 # 상위 주문 상품 조회 개수
```

#### 3.2.2. 카디널리티 분석
카디널리티는 product_id가 quantity보다 높다고 추정됩니다.

아래는 count 쿼리를 통해 더미데이터에 대한 카디널리티를 분석한 결과입니다.
- `count` 쿼리
```sql
select
    count(distinct(product_id)),
    count(distinct(quantity))
from order_product;
```

- 결과

| count\(distinct\(product\_id\)\) | count\(distinct\(quantity\)\) |
| :--- | :--- |
| 10000 | 100 |

현재 데이터에서 상품은 10,000개이고, 최대 재고는 100개입니다. 따라서, `product_id`가 카디널리티가 높습니다.

#### 3.2.3. 적용할 인덱스 선정
인덱스 컬럼 순서는 아래와 같이 결정할 수 있습니다.
- 첫 번째: `created_at`
  - WHERE 조건에 사용되므로 인덱스의 선두로 설정하여 범위 검색(range scan)을 빠르게 처리합니다.
- 두 번째: `product_id`
  - GROUP BY에 사용되므로, 인덱스 스캔 시 이미 정렬된 상태로 데이터를 가져와 그룹화를 효율화합니다.
- 세 번째: `quantity`
  - SELECT 절의 sum(quantity) 계산에 사용되며, 인덱스에 포함시키면 커버링 인덱스로 작용하여 테이블 리딩 비용을 줄일 수 있습니다.

## 4. 성능 개선 테스트
이번 보고서에서 사용된 시나리오는 각각 2개의 컬럼을 가지고 인덱스를 생성합니다. 각 인덱스의 순서에 따라 조회 속도가 얼마나 개선이 되는 지 확인해보겠습니다. 
### 4.1. 사용자 쿠폰 조회
#### 4.1.1. 전제 
- 조회 테이블
  - `user_coupon`
- 전체 데이터 수
  - 11,200,000개
- 카디널리티
  - `user_id`: 약 36만
  - `user_coupon_status`: 4

#### 4.1.2. 조회 테스트
- 인덱스가 없는 경우
  - 소요시간: 4000ms
  - 실행계획
  
    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
    | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
    | 1 | SIMPLE | user\_coupon | null | ALL | null | null | null | null | 10628111 | 2.5 | Using where |
- 인덱스 `user_id`, `user_coupon_status`
  - 소요시간: 130ms
  - 실행계획
  
    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
    | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
    | 1 | SIMPLE | user\_coupon | null | ref | idx\_user\_id\_user\_coupon\_status | idx\_user\_id\_user\_coupon\_status | 9 | const,const | 32 | 100 | Using index condition |
- 인덱스 `user_coupon_status`, `user_id`
  - 소요시간: 130mx
  - 실행계획
  
    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
    | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
    | 1 | SIMPLE | user\_coupon | null | ref | idx\_user\_coupon\_status\_user\_id | idx\_user\_coupon\_status\_user\_id | 9 | const,const | 32 | 100 | Using index condition |

#### 4.1.3. 결과
어떤 인덱스를 적용하더라도 인덱스가 없는 것보다 조회 실행 속도가 약 30배 빨라졌습니다. 하지만 인덱스의 순서가 바뀌더라도 소요시간이 비슷하고 실행계획도 동일했습니다.

제가 조회할 때 사용한 쿼리는 

```sql
select  
    id,  
    coupon_id,  
    created_at,  
    expired_date,  
    updated_at,  
    use_date,  
    user_coupon_status,  
    user_id  
from  
    user_coupon  
where  
    user_id = 511  
    and user_coupon_status = 'ISSUED';
```
였습니다. 여기서 `where` 절을 보시면 두 컬럼 모두 '=' 으로 데이터를 찾는 것을 확인하실 수 있습니다. 복합 인덱스이더라도, 인덱스의 모든 컬럼이 '=' 비교를 하고 있다면 거의 동일한 성능을 냅니다.

이 상황을 좀 더 극적으로 테스트하고 싶다면, 각 컬럼을 단일 인덱스로 생성해보면 됩니다.
`user_id`를 단일 인덱스로 하였을 경우, 테스트 때와 마찬가지로 130ms 정도의 결과가 도출되었습니다. 하지만 `user_coupon_status`를 단일 인덱스로 한 경우 7,000ms로 확연한 차리를 보여줬습니다.

### 4.2. 상위 주문 상품 조회

#### 4.2.1. 전제
- 조회 테이블
  - `order_product`
- 전체 데이터 수
  - 51,200,017개
- 카디널리티
  - `product_id`: 10,000 
  - `quantity`: 100
#### 4.2.2. 조회 테스트
- 인덱스가 없는 경우
  - 소요시간: 13,000ms
  - 실행계획

    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
      | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
      | 1 | SIMPLE | order\_product | null | ALL | null | null | null | null | 49226851 | 33.33 | Using where; Using temporary; Using filesort |
- 인덱스 `quantity`, `created_at`, `product_id`
  - 소요시간: 17,000ms
  - 실행계획
  
    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
    | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
    | 1 | SIMPLE | order\_product | null | index | idx\_quantity\_product\_id | idx\_quantity\_product\_id | 22 | null | 49226851 | 33.33 | Using where; Using index; Using temporary; Using filesort |

- 인덱스 `created_at`, `quantity`, `product_id`
  - 소요시간: 282ms
  - 실행계획
  
    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
    | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
    | 1 | SIMPLE | order\_product | null | range | idx\_created\_at\_quantity\_product\_id | idx\_created\_at\_quantity\_product\_id | 9 | null | 824108 | 100 | Using where; Using index; Using temporary; Using filesort |
- 인덱스  `product_id`, `quantity`, `created_at`
  - 소요시간: 17,000ms
  - 실행계획
  
    | id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
    | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
    | 1 | SIMPLE | order\_product | null | index | idx\_product\_id\_quantity | idx\_product\_id\_quantity | 22 | null | 49226851 | 33.33 | Using where; Using index; Using temporary; Using filesort |

#### 4.2.3. 결과
테스트에 사용한 쿼리는 다음과 같습니다.
```sql
select
    product_id,
    sum(quantity)
from
    order_product
where
    created_at > '2025-02-09'
group by
    product_id
order by
    sum(quantity) desc
limit 3;
```

`3.2.3. 적용할 인덱스 선정` 에서 언급했 듯 `created_at`, `quantity`, `product_id`를 사용하는 것이 약 12초의 실행 시간이 감소되어 가장 효율적인 인덱스입니다.

그런데, 나머지 두 개는 인덱스가 있는 것이 없는 것보다 성능이 좋지 않습니다. 이유는, 왼쪽(선두) 컬럼이 쿼리의 조건과 맞아야 인덱스 효율이 좋다는 특징 때문입니다.
때문에 불필요한 풀 스캔, 정렬 등이 발생합니다. 

```sql
create index idx_quantity_product_id on order_product (quantity, product_id);
```
| id | select\_type | table | partitions | type | possible\_keys | key | key\_len | ref | rows | filtered | Extra |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | SIMPLE | order\_product | null | ALL | idx\_quantity\_product\_id | null | null | null | 49226851 | 33.33 | Using where; Using temporary; Using filesort |

## 5. 결론
이번 보고서에서는 제 E-commerce 프로젝트에서 인덱스를 생성하면 좋은 시나리오를 파악하고, 인덱스를 생성한 후 성능 개선 정도를 간단하게 테스트해봤습니다.

DB의 조회 성능을 최적화하기 위해서는 **적절한** 인덱스를 사용해야함을 알 수 있었습니다. 인덱스를 아무렇게나 적용하면 오히려 성능이 저하될 수 있는 것도 확인하였습니다.
따라서, 인덱스는 단순하게 적용만 해서 될 일이 아니라 사용하고자 하는 쿼리를 잘 분석하는 것이 중요합니다.
