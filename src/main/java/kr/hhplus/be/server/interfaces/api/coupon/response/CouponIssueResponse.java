package kr.hhplus.be.server.interfaces.api.coupon.response;

public record CouponIssueResponse(
        Long id,
        String name,
        String type,
        Integer discountAmount,
        Integer discountRate
) {
}
