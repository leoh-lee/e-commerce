package kr.hhplus.be.server.support.http.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 10XX: 포인트
    SUCCESS_CHARGE_POINT("1000", "포인트 충전 성공")
    , SUCCESS_SEARCH_USER_POINT("1001", "사용자 포인트 조회 성공")

    // 11XX: 상품
    , SUCCESS_SEARCH_PRODUCTS("1100", "상품 조회 성공")

    // 12XX: 쿠폰
    , SUCCESS_ISSUE_COUPON("1200", "쿠폰 발급 성공")
    , SUCCESS_SEARCH_USER_COUPON("1201", "사용자 쿠폰 목록 조회 성공")
    , SUCCESS_SEARCH_AVAILABLE_COUPON("1202", "발급 가능한 쿠폰 목록 조회 성공")

    // 13XX: 주문
    , SUCCESS_ORDER("1300", "주문 성공")
    , SUCCESS_SEARCH_ORDERS("1301", "주문 목록 조회")
    , SUCCESS_SEARCH_TOP_ORDERS("1302", "상위 주문 상품 조회")

    // 14XX: 결제
    , SUCCESS_PAYMENT("1400", "결제 성공")
    , SUCCESS_SEARCH_PAYMENT("1401", "사용자별 결제 내역 조회")
    ;

    private final String code;
    private final String message;

}
