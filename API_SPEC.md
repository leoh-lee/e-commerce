# API 명세서

## 목차
1. [개요](#개요)
2. [잔액 충전](#1-잔액-충전)
3. [잔액조회](#2-잔액조회)
4. [상품 목록 조회](#3-상품-목록-조회)
5. [쿠폰 발급](#4-쿠폰-발급)
6. [쿠폰 목록 조회](#5-쿠폰-목록-조회)
7. [주문](#6-주문)
8. [결제](#7-결제)
9. [주문 목록 조회](#8-주문-목록-조회)
10. [상위 상품 조회](#9-상위-상품-조회)

## 개요
  - **API Prefix**: `/api/v1`
  - **설명**: e-commerce 서비스를 위해 작성된 API 명세서입니다. 
  - **Base URL**: `localhost:8080`
---

## 1. 잔액 충전
  - **URL**: `/points`
  - **Method**: POST
  - **Description**: 사용자의 잔액을 충전하는 API

### 요청(Request)
#### **Headers**
  | Key          | Value            | 필수 여부 | 설명           |
  |--------------|------------------|---------|----------------|
  | Content-Type | application/json | 필수      | 요청 데이터 형식 |

#### **Request Body**
  | 필드이름 | 데이터 타입 | 필수 여부 |   설명   |
  |--------|----------|---------|---------|
  | userId | long     | 필수     | 사용자 ID |
  | amount | integer  | 필수     | 충전 금액 |

### 응답(Response)
#### **Response Body**
  | 필드 이름    | 데이터 타입 | 설명          | 필수 여부 | 예제 값     |
  |------------|----------|--------------|---------|------------|
  | code       | integer  | 상태 코드      | 필수     | 200           |
  | message    | string   | 응답 메시지     | 필수     | "충전 성공"    |
  | result     | object   | 결과 데이터 객체 | 필수     | -             |
  | ├─ userId  | long     | 사용자 ID      | 필수     | 1             |
  | ├─ amount  | integer  | 충전 금액       | 필수      | 10000         |

### 예시(Example)
#### Request
  ```http
  POST /api/v1/points HTTP/1.1
  Host: api.example.com
  Content-Type: application/json

  {
      "userId": 1,
      "amount": 10000
  }
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "충전 성공",
      "result": {
          "userId": 1,
          "amount": 10000
      }
  }
  ```

## 2. 잔액조회

### 기본 정보
  - **URL**: `/points/{userId}`
  - **Method**: GET
  - **Description**: 사용자의 현재 잔액을 조회하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Path Variables**
  | 필드이름           | 데이터 타입                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | userId | long       | 필수      | 사용자 ID       |

### 응답(Response)
#### **Response Body**
  |   필드 이름  |   데이터 타입  |     설명      | 필수 여부 |   예제 값  |
  |------------|-------------|--------------|---------|----------|
  | code       | integer     | 상태 코드      | 필수     | 200       |
  | message    | string      | 응답 메시지     | 필수     | "조회 성공" |
  | result     | object      | 결과 데이터 객체 | 필수     | -         |
  | ├─ userId  | long        | 사용자 ID      | 필수     | 1         |
  | └─ balance | integer     | 현재 잔액       | 필수     | 50,000    |

### 예시(Example)
#### Request
```http
GET /api/v1/points/1 HTTP/1.1
Host: api.example.com
Content-Type: application/json
```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "조회 성공",
      "result": {
          "userId": 1,
          "balance": 50000
      }
  }
  ```

## 3. 상품 목록 조회

### 기본 정보
  - **URL**: `/products`
  - **Method**: GET
  - **Description**: 상품 목록을 조회하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

### 응답(Response)
#### **Response Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드             | 필수      | 200           |
  | message    | string      | 응답 메시지           | 필수      | "조회 성공"    |
  | result     | object      | 결과 데이터 객체      | 필수      | -             |
  | ├─ id   | long        | 상품 ID              | 필수      | 1             |
  | ├─ name | string      | 상품명               | 필수      | "상품1"        |
  | ├─ price| integer     | 상품 가격            | 필수      | 10,000         |
  | └─ stock| integer     | 재고 수량            | 필수      | 100           |

### 예시(Example)
#### Request
  ```http
  GET /api/v1/products HTTP/1.1
  Host: api.example.com
  Content-Type: application/json
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "조회 성공",
      "result": [
          {
              "id": 1,
              "name": "상품1",
              "price": 10000,
              "stock": 100
          },
          {
              "id": 2,
              "name": "상품2",
              "price": 20000,
              "stock": 200
          }
      ]
  }
  ```

## 4. 쿠폰 발급

### 기본 정보
  - **URL**: `/coupons`
  - **Method**: POST
  - **Description**: 쿠폰을 발급하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Request Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | userId | long        | 사용자 ID            | 필수      | 1             |
  | couponId | long        | 쿠폰 ID            | 필수      | 1             |

### 응답(Response)
#### **Response Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드             | 필수      | 200           |
  | message    | string      | 응답 메시지           | 필수      | "쿠폰 발급 성공"    |
  | result     | object      | 결과 데이터 객체      | 필수      | -             |
  | ├─ id   | long        | 쿠폰 ID              | 필수      | 1             |
  | ├─ name | string      | 쿠폰명               | 필수      | "10% 할인 쿠폰"        |
  | ├─ type | string     | 쿠폰 타입 (FIXED, PERCENTAGE)           | 필수      | "PERCENTAGE"        |
  | ├─ discountAmount | integer | 정액 할인 금액(type이 FIXED일 때)    | 조건부 필수      | 10000 |
  | └─ discountRate | integer | 정률 할인율(type이 PERCENTAGE일 때)    | 조건부 필수      | 10    |

### 예시(Example)
#### Request
  ```http
  POST /api/v1/coupons HTTP/1.1
  Host: api.example.com
  Content-Type: application/json

  {
      "userId": 1,
      "couponId": 1
  }
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "쿠폰 발급 성공",
      "result": {
          "id": 1,
          "name": "10% 할인 쿠폰",
          "type": "PERCENTAGE",
          "discountAmount": 10000
      }
  }
  ```

## 5. 쿠폰 목록 조회

### 기본 정보
  - **URL**: `/coupons/{userId}`
  - **Method**: GET
  - **Description**: 사용자의 쿠폰 목록을 조회하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Path Variables**
  | 필드이름 | 데이터 타입 | 필수 여부 | 설명     |
  |--------|----------|---------|---------|
  | userId | long     | 필수     | 사용자 ID |

### 응답(Response)
#### **Response Body**
  | 필드 이름    | 데이터 타입    | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드             | 필수      | 200           |
  | message    | string      | 응답 메시지           | 필수      | "조회 성공"    |
  | result     | array       | 결과 데이터 리스트      | 필수      | -             |
  | ├─ id      | long        | 쿠폰 ID              | 필수      | 1             |
  | ├─ name    | string      | 쿠폰명               | 필수      | "10% 할인 쿠폰"        |
  | ├─ type    | string     | 쿠폰 타입 (FIXED, PERCENTAGE)           | 필수      | "PERCENTAGE"        |
  | ├─ discountAmount | integer     | 정액 할인 금액(type이 FIXED일 때)    | 조건부 필수      | 10000         |
  | └─ discountRate | integer     | 정률 할인율(type이 PERCENTAGE일 때)    | 조건부 필수      | 10         |

### 예시(Example)
#### Request
  ```http
  GET /api/v1/coupons/1 HTTP/1.1
  Host: api.example.com
  Content-Type: application/json
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "조회 성공",
      "result": [
          {
              "id": 1,
              "name": "10% 할인 쿠폰",
              "type": "PERCENTAGE",
              "discountRate": 10
          },
          {
              "id": 2,
              "name": "10,000원 할인 쿠폰",
              "type": "FIXED",
              "discountAmount": 10000
          }
      ]
  }
  ```

## 6. 주문
### 기본 정보
  - **URL**: `/orders`
  - **Method**: POST
  - **Description**: 주문을 생성하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Request Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | userId | long        | 사용자 ID            | 필수      | 1             |
  | productId | long        | 상품 ID            | 필수      | 1             |
  | quantity | integer     | 주문 수량            | 필수      | 5             |
  | couponId | long        | 쿠폰 ID            | 조건부 필수      | 1             |

### 응답(Response)
#### **Response Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드            | 필수      | 200           |
  | message    | string      | 응답 메시지          | 필수      | "주문 생성 성공"    |
  | result     | object      | 결과 데이터 객체     | 필수      | -             |
  | ├─ id      | long        | 주문 ID             | 필수      | 1             |
  | ├─ userId  | long        | 사용자 ID           | 필수      | 1             |
  | ├─ productId | long        | 상품 ID           | 필수      | 1             |
  | ├─ quantity | integer     | 주문 수량           | 필수      | 1             |
  | ├─ couponId | long        | 쿠폰 ID           | 조건부 필수      | 1             |
  | ├─ totalPrice | integer     | 총 주문 금액           | 필수      | 10000         |
  | ├─ discountAmount | integer     | 할인 금액           | 조건부 필수      | 1000         |
  | ├─ finalPrice | integer     | 최종 주문 금액           | 필수      | 9000         |
  | ├─ orderDate | string      | 주문 일시           | 필수      | "2024-01-01 10:00:00"         |
  | └─ orderStatus | string      | 주문 상태           | 필수      | "PENDING"         |

### 예시(Example)
#### Request
  ```http
  POST /api/v1/orders HTTP/1.1
  Host: api.example.com
  Content-Type: application/json

  {
      "userId": 1,
      "productId": 1,
      "quantity": 5,
      "couponId": 1
  }
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "주문 생성 성공",
      "result": {
          "id": 1,
          "userId": 1,
          "productId": 1,
          "quantity": 1,
          "couponId": 1,
          "totalPrice": 10000,
          "discountAmount": 1000,
          "finalPrice": 9000,
          "orderDate": "2024-01-01 10:00:00",
          "orderStatus": "PENDING"
      }
  }
  ```

## 7. 결제
### 기본 정보
  - **URL**: `/orders/{orderId}/payment`
  - **Method**: POST
  - **Description**: 주문을 결제하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Request Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | orderId | long        | 주문 ID            | 필수      | 1             |

### 응답(Response)
#### **Response Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드             | 필수      | 200           |
  | message    | string      | 응답 메시지           | 필수      | "결제 성공"    |

### 예시(Example)
#### Request
  ```http
  POST /api/v1/orders/1/payment HTTP/1.1
  Host: api.example.com
  Content-Type: application/json

  {
      "orderId": 1
  }
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "결제 성공"
  }
  ```

## 8. 주문 목록 조회
### 기본 정보
  - **URL**: `/orders`
  - **Method**: GET
  - **Description**: 주문 목록을 조회하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Query Parameters**
  | 필드이름           | 데이터 타입                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | userId | long       | 필수      | 사용자 ID       |

#### **Response Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드             | 필수      | 200           |
  | message    | string      | 응답 메시지           | 필수      | "조회 성공"    |
  | result     | array       | 결과 데이터 리스트      | 필수      | -             |
  | ├─ id   | long        | 주문 ID              | 필수      | 1             |
  | ├─ userId | long        | 사용자 ID            | 필수      | 1             |
  | ├─ productId | long        | 상품 ID            | 필수      | 1             |
  | ├─ quantity | integer     | 주문 수량            | 필수      | 1             |
  | ├─ couponId | long        | 쿠폰 ID            | 조건부 필수      | 1             |
  | ├─ totalPrice | integer     | 총 주문 금액            | 필수      | 10000         |
  | ├─ discountAmount | integer     | 할인 금액            | 조건부 필수      | 1000         |
  | ├─ finalPrice | integer     | 최종 주문 금액            | 필수      | 9000         |
  | ├─ orderDate | string      | 주문 일시            | 필수      | "2024-01-01 10:00:00"         |
  | └─ orderStatus | string      | 주문 상태            | 필수      | "PENDING"         |

### 예시(Example)
#### Request
  ```http
  GET /api/v1/orders?userId=1 HTTP/1.1
  Host: api.example.com
  Content-Type: application/json
  ```
  #### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "조회 성공",
      "result": [
          {
              "id": 1,
              "userId": 1,
              "productId": 1,
              "quantity": 5,
              "couponId": 1,
              "totalPrice": 10000,
              "discountAmount": 1000,
              "finalPrice": 9000,
              "orderDate": "2024-01-01 10:00:00",
              "orderStatus": "PENDING"
          }
      ]
  }
  ```

## 9. 상위 상품 조회
### 기본 정보
  - **URL**: `/products/top`
  - **Method**: GET
  - **Description**: 상위 상품을 조회하는 API

### 요청(Request)
#### **Headers**
  | Key           | Value                 | 필수 여부 | 설명           |
  |---------------|-----------------------|-----------|----------------|
  | Content-Type  | application/json     | 필수      | 요청 데이터 형식 |

#### **Response Body**
  | 필드 이름  | 데이터 타입 | 설명                  | 필수 여부 | 예제 값       |
  |------------|-------------|-----------------------|-----------|---------------|
  | code       | integer     | 상태 코드             | 필수      | 200           |
  | message    | string      | 응답 메시지           | 필수      | "조회 성공"    |
  | result     | array       | 결과 데이터 리스트      | 필수      | -             |
  | ├─ id      | long        | 상품 ID              | 필수      | 1             |
  | ├─ name    | string      | 상품명               | 필수      | "상품1"        |
  | ├─ price   | integer     | 상품 가격            | 필수      | 10,000         |
  | └─ stock   | integer     | 재고 수량            | 필수      | 100           |

### 예시(Example)
#### Request
  ```http
  GET /api/v1/products/top HTTP/1.1
  Host: api.example.com
  Content-Type: application/json
  ```
#### Response
  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json

  {
      "code": 200,
      "message": "조회 성공",
      "result": [
          {
              "id": 1,
              "name": "상품1",
              "price": 10000,
              "stock": 100
          }
      ]
  }
  ```