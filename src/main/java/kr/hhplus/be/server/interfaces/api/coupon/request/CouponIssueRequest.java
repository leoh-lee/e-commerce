package kr.hhplus.be.server.interfaces.api.coupon.request;

public record CouponIssueRequest(
        Long userId,
        Long couponId
) {
}
