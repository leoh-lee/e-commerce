package kr.hhplus.be.server.support.http.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 2XXX: 성공

        // 20XX: 포인트
        SUCCESS_CHARGE_POINT("2000", "포인트 충전에 성공했습니다.")
        , SUCCESS_SEARCH_USER_POINT("2001", "사용자 포인트 조회에 성공했습니다.")

        // 21XX: 상품
        , SUCCESS_SEARCH_PRODUCTS("2100", "상품 목록 조회에 성공했습니다.")

        // 22XX: 쿠폰
        , SUCCESS_ISSUE_COUPON("2200", "쿠폰을 성공적으로 발급했습니다.")
        , SUCCESS_SEARCH_USER_COUPON("2201", "사용자 쿠폰 목록 조회에 성공했습니다.")
        , SUCCESS_SEARCH_AVAILABLE_COUPON("2202", "발급 가능한 쿠폰 목록 조회 성공")

        // 23XX: 주문
        , SUCCESS_ORDER("2300", "주문 성공")
        , SUCCESS_SEARCH_ORDERS("2301", "주문 목록 조회")
        , SUCCESS_SEARCH_TOP_ORDERS("2302", "상위 주문 상품 조회에 성공했습니다.")

        // 24XX: 결제
        , SUCCESS_PAYMENT("2400", "결제 성공")
        , SUCCESS_SEARCH_PAYMENT("2401", "사용자별 결제 내역 조회")

    // 4XXX: 클라이언트 에러
        , EXPIRED_COUPON("4200", "만료된 쿠폰입니다.")
        , INVALID_COUPON("4201", "유효하지 않은 쿠폰입니다.")

        , USER_NOT_FOUND("4000", "사용자를 찾을 수 없습니다.")
        , COUPON_NOT_FOUND("4001", "쿠폰을 찾을 수 없습니다.")
        , POINT_NOT_FOUND("4002", "포인트를 찾을 수 없습니다.")
        , ORDER_NOT_FOUND("4003", "주문을 찾을 수 없습니다.")
        , PAYMENT_NOT_FOUND("4004", "결제를 찾을 수 없습니다.")
        , PRODUCT_NOT_FOUND("4005", "상품을 찾을 수 없습니다.")

        , POINT_NOT_ENOUGH("4100", "포인트가 부족합니다.")

    // 5XXX: 서버 에러
    // 최대 포인트 이상 포인트를 충전할 수 없다는 메시지
        , POINT_CHARGE_FAILED("5000", "포인트 충전 실패")
        , MAXIMUM_POINT_LIMIT_EXCEEDED("5001", "최대 충전 가능 포인트를 초과했습니다.")
        , USER_POINT_SEARCH_FAILED("5002", "사용자 포인트 조회 실패")
        , PRODUCT_SEARCH_FAILED("5100", "상품 목록 조회에 실패했습니다.")
        , PRODUCT_TOP_SEARCH_FAILED("5101", "상위 상품 조회에 실패했습니다.")
        , COUPON_EXPIRED("5200", "만료된 쿠폰입니다.")
        , COUPON_INVALID("5201", "유효하지 않은 쿠폰입니다.")
        , ORDER_SEARCH_FAILED("5300", "주문 목록 조회에 실패했습니다.")

        , INSUFFICIENT_BALANCE("5400", "결제 잔액이 부족합니다.")

    ;

    private final String code;
    private final String message;

}
