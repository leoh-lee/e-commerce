package kr.hhplus.be.server.support.http.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 2XXX: 성공

        // 20XX: 포인트
        SUCCESS_CHARGE_POINT("2000", "포인트 충전 성공")
        , SUCCESS_SEARCH_USER_POINT("2001", "사용자 포인트 조회 성공")

        // 21XX: 상품
        , SUCCESS_SEARCH_PRODUCTS("2100", "상품 조회 성공")

        // 22XX: 쿠폰
        , SUCCESS_ISSUE_COUPON("2200", "쿠폰 발급 성공")
        , SUCCESS_SEARCH_USER_COUPON("2201", "사용자 쿠폰 목록 조회 성공")
        , SUCCESS_SEARCH_AVAILABLE_COUPON("2202", "발급 가능한 쿠폰 목록 조회 성공")

        // 23XX: 주문
        , SUCCESS_ORDER("2300", "주문 성공")
        , SUCCESS_SEARCH_ORDERS("2301", "주문 목록 조회")
        , SUCCESS_SEARCH_TOP_ORDERS("2302", "상위 주문 상품 조회")

        // 24XX: 결제
        , SUCCESS_PAYMENT("2400", "결제 성공")
        , SUCCESS_SEARCH_PAYMENT("2401", "사용자별 결제 내역 조회")

    // 4XXX: 클라이언트 에러

    // 5XXX: 서버 에라
    ;

    private final String code;
    private final String message;

}
