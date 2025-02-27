# 부하테스트 계획 보고서

## 1. 개요
본 보고서에서는 E-commerce 프로젝트에서 부하테스트가 필요한 대상을 선정하고, 테스트 목적, 시나리오를 계획해보도록 하겠습니다. 또한, K6를 사용한 테스트 스크립트를 작성하여 수행합니다.

## 2. 부하테스트 대상 선정
부하테스트의 대상을 선정하기 위해 프로젝트의 API를 리스트업하고, 진행할 API를 선정해보겠습니다.
### 2.1. E-commerce 프로젝트 API 목록
먼저, E-commerce 프로젝트에 구현된 API 목록을 알아보겠습니다.

- 쿠폰
    - 쿠폰 발급
    - 사용자 쿠폰 조회
    - 발급 가능한 쿠폰 조회
- 주문
    - 상품 주문
    - 주문 조회
    - 상위 주문 상품 조회
- 결제
    - 결제
    - 결제 내역 조회
- 포인트
    - 포인트 충전
    - 포인트 조회
- 상품
    - 상품 목록조회

### 2.2. 부하테스트 API 대상 선정

#### 2.2.1. 선정 기준
부하테스트의 대상은 다음과 같은 기준으로 선정합니다.
1. 부하가 집중되는 API인가?
2. 병목이 예상되는 API인가?
3. Slow query가 예상되는 API인가?

#### 2.2.2. 테스트 대상
1. **부하가 집중되는 API**
    - **상품 목록조회**
      이커머스는 기본적으로 상품을 조회하는데에서부터 유저들의 시나리오가 시작됩니다. 따라서, 부하가 집중되는 테스트 대상으로 선정하였습니다.
    - **쿠폰 발급**
      쿠폰 발급은 선착순 쿠폰 발급이기 때문에 단기적으로 부하가 발생할 수 있는 API입니다. 특히, 해당 API에서는 Redis를 활용하기 때문에 Redis 전략설정 및 최적화가 필요하다고 판단하여 테스트 대상으로 선정하였습니다.
2. **병목이 예상되는 API**
    - **상품 주문**
      상품 주문은 트랜잭션의 범위가 넓습니다. 사용자, 상품, 재고, 쿠폰, 데이터 플랫폼 전송의 로직이 한 번에 수행되어야 합니다. 게다가, 상품 재고 차감은 분산락으로 구현되어있고, 데이터 플랫폼에서 이벤트 리스너와 카프라를 사용하기 때문에 다양한 병목지점이 예상됩니다. 따라서 테스트 대상으로 선정하였습니다.
3. **Slow query가 예상되는 API**
    - **상위 주문 상품 조회**
      상위 주문 상품 조회는 Slow query가 예상되어 이미 Redis 캐싱을 통해 처리한 바 있습니다. 하지만 이에 대한 성능테스트가 미흡했기 때문에, 테스트 대상으로 선정하였습니다.

## 3. 부하테스트 시나리오 설계
> 본 섹션에서 부하 테스트에 사용하는 가상 유저의 수를 VU라고 하겠습니다.
### 3.1. 상품목록 조회
#### 3.1.1. 시나리오
1. VU를 1000명으로 30초간 서서히 증가시킵니다.
2. VU가 2000명으로 20초간 증가합니다.
3. VU가 1000명으로 2초간 감소합니다.
4. VU가 1000명으로 2분간 유지합니다.
#### 3.1.2. 이유
상품 목록 조회는 꾸준히 부하가 발생하는 API입니다. 기본적인 Load Test를 통해 부하테스트로, 특정 기간동안의 꾸준한 부하에도 성능을 낼 수 있는 지 확인할 수 있습니다.

#### 3.1.3. 스크립트
```js
import http from 'k6/http';  
import { sleep, check } from 'k6';  
  
export const options = {  
	stages: [  
		{  
			target: 2000,  
			duration: '30s'  
		},  
		{  
			target: 3000,  
			duration: '10s'  
		},  
		{  
			target: 2000,  
			duration: '2s'  
		},  
		{  
			target: 2000,  
			duration: '2m'  
		},
	]  
};  
	  
const BASE_URL = "http://host.docker.internal:8080";  
	  
export default function() {  
	const params = {  
		headers: {  
			'Content-Type': 'application/json'  
		}  
	}  
	
	let res = http.get(`${BASE_URL}/api/v1/products`, params);  
	check(res, { "status is 200": (res) => res.status === 200 });  
	sleep(1);  
}
```
### 3.2. 쿠폰 발급
#### 3.2.1. 시나리오
1. 1초간 2000명의 VU을 단시간에 발생시킵니다.
2. 10초간 10,000명의 VU로 증가시킵니다.
#### 3.2.2. 이유
선착순 쿠폰 발급이므로, 굉장히 짧은 시간에 폭발적인 요청이 있을 것으로 예상됩니다. 이렇게 테스트 함으로써, 쿠폰 발급 개시하자마자 1초간 2,000 명의 가상 유저 요청하는 경우와 첫 개시 이후 소강상태의 경우를 테스트할 수 있습니다.
#### 3.2.3. 스크립트
```js
import http from 'k6/http';  
import { sleep, check } from 'k6';  
	  
export const options = {  
    stages: [  
        {  
            target: 2000,  
            iterations: 1,  
            duration: '1s'  
        },  
        {  
            target: 10000,  
            iterations: 1,  
            duration: '10s'  
        }    
    ]
};  
  
const BASE_URL = "http://host.docker.internal:8080";  
  
export default function() {  
    let userId = ( (__VU - 1) * 100 ) + __ITER + 30000;  
  
    const payload = JSON.stringify({  
        userId: userId,  
        couponId: 1  
    });  
  
    const params = {  
        headers: {  
            'Content-Type': 'application/json'  
        }  
    }  
  
    let res = http.post(`${BASE_URL}/api/v1/coupons`, payload, params);  
    check(res, { "status is 200": (res) => res.status === 200 });  
    sleep(1);  
}
```
### 3.3. 상품 주문
#### 3.3.1. 시나리오
1. VU를 400명으로 10초간 서서히 증가시킵니다.
2. VU를 1000명으로 2초간 급작스럽게 증가시킵니다.
3. VU가 다시 400명으로 서서히 1분간 감소합니다.
4. VU가 400명으로 1분간 유지됩니다.
#### 3.3.2. 이유
상품 주문은 상품목록 조회와 마찬가지고 e-commerce의 중요한 비즈니스이며, 꾸준히 부하가 발생하는 API입니다. 상품 목록 조회보다는 부하가 적을 것으로 예상되므로 상품 조회 5명 당 1명인 평균 400명의 요청이 있을 것이라 가정하였습니다.
#### 3.3.3. 스크립트
```js
import http from 'k6/http';  
import { sleep, check } from 'k6';  
  
export const options = {  
    stages: [  
		{  
		    target: 400,  
		    duration: '10s'  
		},  
		{  
		    target: 1000,  
		    duration: '2s'  
		},  
		{  
		    target: 400,  
		    duration: '1m'  
		},  
		{  
		    target: 400,  
		    duration: '1m'  
		}
    ]  
};  
  
const BASE_URL = "http://host.docker.internal:8080";  
  
export default function() {  
    const payload = JSON.stringify({  
        userId: 1,  
        products: [  
            {  
                "productId": 1,  
                "quantity": 5  
            }  
        ],  
        userCouponId: null  
    });  
  
    const params = {  
        headers: {  
            'Content-Type': 'application/json'  
        }  
    }  
  
    let res = http.post(`${BASE_URL}/api/v1/orders`, payload, params);  
    check(res, { "status is 200": (res) => res.status === 200 });  
    sleep(1);  
}
```

### 3.4. 상위 주문 상품 조회
#### 3.4.1. 시나리오
1. VU를 2000명으로 30초간 서서히 증가시킵니다.
2. VU가 3000명으로 10초간 급작스럽게 증가합니다.
3. 2000명 VU를 2분간 유지합니다.
#### 3.4.2. 이유
상위 주문 상품 조회 API는 상품 목록 조회와 동일한 화면에서 가져올 것으로 예상됩니다. 따라서, 상품 목록 조회 API와 동일한 부하테스트를 진행하도록 하겠습니다.

#### 3.4.3. 스크립트

```js
import http from 'k6/http';  
import { sleep, check } from 'k6';  
  
export const options = {  
    stages: [  
		{  
		    target: 2000,  
		    duration: '30s'  
		},  
		{  
		    target: 3000,  
		    duration: '10s'  
		},  
		{  
		    target: 2000,  
		    duration: '2m'  
		}, 
    ]  
};  
  
const BASE_URL = "http://host.docker.internal:8080";  
  
export default function() {  
    const params = {  
        headers: {  
            'Content-Type': 'application/json'  
        }  
    }  
  
    let res = http.get(`${BASE_URL}/api/v1/orders/top`, params);  
    check(res, { "status is 200": (res) => res.status === 200 });  
    sleep(1);  
}
```

## 4. 나가며
이번 보고서에서는 E-commerce 프로젝트에서의 부하테스트 대상을 선정하였습니다. 또한 각 대상에 대한 테스트 시나리오를 설계하고 스크립트를 작성하였습니다. E-commerce 프로젝트는 레디스, 카프카 등을 활용하여 대용량 트래픽을 고려하여 작성하긴 했습니다. 하지만, 실제로 대용량 트래픽을 받아보진 못했습니다. 다음 보고서에서는 서비스에 부하를 준 뒤, 문제가 되는 병목 지점을 파악하고 이를 개선해보도록 하겠습니다.
