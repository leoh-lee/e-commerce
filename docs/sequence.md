# e-commerce sequence diagram

## 개요
이커머스 서비스 개발을 위한 시퀀스 다이어그램 목록입니다. 모든 다이어그램은 mermaid를 사용하여 작성되었습니다.

> mermaid 문법 참고: [mermaid 공식 문서](https://mermaid.js.org/)

## 목차
1. [잔액 충전](#1-잔액-충전)
2. [잔액 조회](#2-잔액-조회)
3. [상품 목록 조회](#3-상품-목록-조회)
4. [쿠폰 발급](#4-쿠폰-발급)
5. [사용자 쿠폰 목록 조회](#5-사용자-쿠폰-목록-조회)
6. [발급 가능한 쿠폰 목록 조회](#6-발급-가능한-쿠폰-목록-조회)
7. [주문](#7-주문)
8. [결제](#8-결제)
9. [주문 내역 조회](#9-주문-내역-조회)
10. [사용자별 결제 내역 조회](#10-사용자별-결제-내역-조회)
11. [상위 상품 조회](#11-상위-상품-조회)

## Sequence Diagram
### 1. 잔액 충전
``` mermaid
%% 잔액 충전

sequenceDiagram

autonumber

actor A as 사용자

participant B as 포인트 Facade

participant C as 사용자 Service

participant D as 포인트 Service

participant E as 포인트 이력 Service

participant F as DB

participant G as 데이터 플랫폼

A->>+B: 잔액 충전 요청

B->>+C: 사용자 조회
C->>+F: 사용자 조회
alt 사용자가 유효하지 않으면
  F-->>A: 예외 발생  
else 사용자가 유효하면
  F-->>-C: 사용자 반환
end
C-->>-B: 사용자 반환
B->>+D: 사용자 잔액 충전

D->>+F: 사용자 잔액 조회

F-->>-D: 사용자 잔액

D->>D: 유효한 포인트 충전인지 확인

alt 유효한 포인트 충전이면

D->>F: 포인트 충전

D-->>B: 충전된 포인트 반환

B->>+E: 포인트 충전 이력 저장

E->>+F: 포인트 충전 이력 저장

E-->>-B: 포인트 충전 이력 반환

B->>G: 포인트 충전 이력 전송

B-->>-A: 성공 응답

else 유효하지 않은 포인트 충전이면

D-->>-A: 예외 결과 반환

end
```

[목차로 돌아가기](#목차)

### 2. 잔액 조회
``` mermaid
%% 잔액 조회
sequenceDiagram

autonumber

actor A as 사용자

participant B as 포인트 Facade

participant C as 사용자 Service

participant D as 포인트 Service

participant E as DB

A->>+B: 사용자 잔액 조회 요청

B->>+C: 사용자 조회

C->>+E: 사용자 조회
alt 사용자가 유효하지 않으면
E->>-A: 예외 반환
else 사용자가 유효하면
E->>C: 사용자 반환
end
C->>-B: 사용자 반환
B->>+D: 사용자 잔액 조회

D->>+E: 사용자 잔액 조회

E-->>-D: 사용자 잔액

D-->>-B: 사용자 잔액

B-->>-A: 사용자 잔액
```

[목차로 돌아가기](#목차)

### 3. 상품 목록 조회
``` mermaid
sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 상품 Facade
    participant C as 상품 Service
    participant D as DB
    A->>+B: 상품 목록 조회
    B->>+C: 상품 목록 조회
    C->>+D: 상품 목록 조회
    D-->>-C: 상품 목록 반환
    C-->>-B: 상품 목록 반환
    B-->>-A: 상품 목록
```

[목차로 돌아가기](#목차)

### 4. 쿠폰 발급
``` mermaid
%% 쿠폰 발급

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 쿠폰 Facade
    participant C as 사용자 Service
    participant D as 쿠폰 Service
    participant E as 쿠폰 이력 서비스 Service
    participant F as DB
    participant G as 데이터 플랫폼
    A->>+B: 쿠폰 발급 요청
    B->>+C: 사용자 조회
    C->>+F: 사용자 조회
    alt 사용자가 유효하지 않으면
    F-->>A: 예외 결과 반환
    else 사용자가 유효하면
    F-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+D: 쿠폰 발급
    D->>+F: 쿠폰 조회
    opt 해당하는 쿠폰이 없으면
        F-->>A: 예외 반환
    end
    F-->>-D: 쿠폰 반환
    D->>D: 쿠폰 유효성 검사
    alt 쿠폰이 유효하지 않으면
        D-->>A: 예외 반환
    else 쿠폰이 유효하면
        D-->>-B: 쿠폰 발급
        B->>+E: 쿠폰 발급 이력 저장
        E->>-F: 쿠폰 발급 이력 저장    
        B->>G: 쿠폰 발급 이력 전송
        B-->>-A: 성공 응답
    end
```

[목차로 돌아가기](#목차)

### 5. 사용자 쿠폰 목록 조회
``` mermaid
%% 사용자 쿠폰 목록 조회

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 쿠폰 Facade
    participant C as 사용자 Service
    participant D as 쿠폰 Service
    participant E as 쿠폰 이력 서비스 Service
    participant F as DB
    A->>+B: 쿠폰 발급 요청
    B->>+C: 사용자 조회
    C->>+F: 사용자 조회
    alt 사용자가 유효하지 않으면
    F-->>A: 예외 결과 반환
    else 사용자가 유효하면
    F-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+E: 사용자 쿠폰 발급 이력 조회
    E-->>-B: 사용자 쿠폰 발급 이력
    B->>+D: 쿠폰 정보 조회
    D-->>-B: 쿠폰 정보 반환
    B-->>-A: 사용자 쿠폰 목록
```

[목차로 돌아가기](#목차)

### 6. 발급 가능한 쿠폰 목록 조회
``` mermaid
%% 발급 가능한 쿠폰 목록 조회

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 쿠폰 Facade
    participant C as 사용자 Service
    participant D as 쿠폰 Service
    participant E as 쿠폰 이력 서비스 Service
    participant F as DB
    A->>+B: 발급 가능한 쿠폰 목록 조회 
    B->>+C: 사용자 조회
    C->>+F: 사용자 조회
    alt 사용자가 유효하지 않으면
    F-->>A: 예외 결과 반환
    else 사용자가 유효하면
    F-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+E: 사용자가 발급받지 않는 쿠폰 목록 조회
    E-->>-B: 사용자 쿠폰 발급 이력
    B->>+D: 쿠폰 정보 조회
    D-->>-B: 쿠폰 정보 반환
    B-->>-A: 사용자 쿠폰 목록
```

[목차로 돌아가기](#목차)

### 7. 주문
``` mermaid
%% 주문

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 주문 Facade
    participant C as 사용자 Service
    participant D as 주문 Service
    participant E as 상품 Service
    participant F as 쿠폰 Service
    participant G as DB
    participant H as 데이터 플랫폼
    A->>+B: 주문 요청
    B->>+C: 사용자 조회
    C->>+G: 사용자 조회
    alt 사용자가 유효하지 않으면
    G-->>A: 예외 결과 반환
    else 사용자가 유효하면
    G-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+E: 상품 조회
    E->>+G: 상품 조회
    alt 상품이 유효하지 않으면
    G-->>A: 예외 결과 반환
    else 상품이 유효하면
    G-->>-E: 상품 정보 반환
    E-->>-B: 상품 정보 반환
    end
    B->>+E: 상품 재고 차감
    E->>-G: 상품 재고 차감
    opt 적용된 쿠폰이 있으면
    B->>+F: 쿠폰 정보 조회
    F->>+G: 쿠폰 정보 조회
    G-->>-F: 쿠폰 정보 반환
    F-->>-B: 쿠폰 정보 반환
    B->>B: 쿠폰 적용
    B->>+F: 쿠폰 상태 업데이트
    F->>-G: 쿠폰 상태 업데이트
    end
    B->>+D: 주문 요청
    D->>-G: 주문 이력 저장
    B->>H: 주문 이력 전송
    B->>-A: 성공 응답
```

[목차로 돌아가기](#목차)

### 8. 결제
``` mermaid
%% 결제

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 결제 Facade
    participant C as 사용자 Service
    participant D as 결제 Service
    participant E as 주문 Service
    participant F as 포인트 Service
    participant G as 포인트 이력 Service
    participant H as DB
    participant I as 데이터 플랫폼
    A->>+B: 결제 요청
    B->>+C: 사용자 조회
    C->>+H: 사용자 조회
    alt 사용자가 유효하지 않으면
    H-->>A: 예외 결과 반환
    else 사용자가 유효하면
    H-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+E: 주문 조회
    E->>+H: 주문 조회
    H-->>-E: 주문 정보 반환
    E-->>-B: 주문 정보 반환
    B->>+D: 결제 요청
    alt 결제 금액보다 잔액이 적으면
    H-->>A: 결제 실패 예외 반환
    else 결제 금액보다 잔액이 적으면
    D->>-H: 결제
    note left of H: 포인트 차감, 결제 데이터 생성
    end
    B->>+E: 주문 상태 업데이트
    E->>-H: 주문 상태 업데이트
    B->>+D: 결제 상태 업데이트
    D->>-H: 결제 상태 업데이트
    B->>+F: 포인트 차감
    F->>+H: 포인트 차감
    H-->>-F: 포인트 차감
    F-->>-B: 포인트 차감
    B->>+G: 포인트 사용 이력 저장
    G->>-H: 포인트 사용 이력 저장
    B->>I: 결제 이력 전송
    B-->>-A: 성공 응답
```

[목차로 돌아가기](#목차)

### 9. 주문 내역 조회
``` mermaid
%% 주문 내역 조회

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 주문 Facade
    participant C as 사용자 Service
    participant D as 주문 Service
    participant E as DB
    A->>+B: 주문 내역 조회
    B->>+C: 사용자 조회
    C->>+E: 사용자 조회
    alt 사용자가 유효하지 않으면
    E-->>A: 예외 결과 반환
    else 사용자가 유효하면
    E-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+D: 주문 내역 조회
    D-->>-B: 주문 내역 반환
    B-->>-A: 주문 내역
```

[목차로 돌아가기](#목차)

### 10. 사용자별 결제 내역 조회
``` mermaid
%% 사용자별 결제 내역 조회

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 결제 Facade
    participant C as 사용자 Service
    participant D as 결제 Service
    participant E as DB
    A->>+B: 사용자별 결제 내역 조회
    B->>+C: 사용자 조회
    C->>+E: 사용자 조회
    alt 사용자가 유효하지 않으면
    E-->>A: 예외 결과 반환
    else 사용자가 유효하면
    E-->>-C: 사용자 반환
    end
    C-->>-B: 사용자 반환
    B->>+D: 사용자별 결제 내역 조회
    D->>+E: 사용자별 결제 내역 조회
    E-->>-D: 사용자별 결제 내역 반환
    D-->>-B: 사용자별 결제 내역 반환
    B-->>-A: 사용자별 결제 내역
```

[목차로 돌아가기](#목차)

### 11. 상위 상품 조회
``` mermaid
%% 상위 상품 조회

sequenceDiagram
    autonumber
    actor A as 사용자
    participant B as 상품 Facade
    participant C as 상품 Service
    participant D as 주문 Service
    participant E as DB

    A->>+B: 상위 주문 조회
    B->>+D: 상위 주문 조회
    note right of D: View를 통해 상위 상품 조회
    D->>+E: 상위 주문 조회
    E-->>-D: 상위 주문 반환
    D-->>-B: 상위 주문 반환
    B->>+C: 상위 주문에 해당하는 상품 조회
    C->>+E: 상위 주문에 해당하는 상품 조회
    E-->>-C: 상위 주문에 해당하는 상품 반환
    C-->>-B: 상위 주문에 해당하는 상품 반환
    B-->>-A: 상위 상품 목록
```

[목차로 돌아가기](#목차)