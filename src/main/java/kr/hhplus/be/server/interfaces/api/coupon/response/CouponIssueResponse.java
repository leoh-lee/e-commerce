package kr.hhplus.be.server.interfaces.api.coupon.response;

import kr.hhplus.be.server.domain.coupon.dto.CouponIssueResult;

public record CouponIssueResponse(
        Long id,
        String name,
        String type,
        Integer discountAmount,
        Integer discountRate
) {

    public static CouponIssueResponse from(CouponIssueResult issueResult) {
        return new CouponIssueResponse(
                issueResult.couponId(),
                issueResult.couponName(),
                issueResult.couponType().name(),
                issueResult.discountAmount(),
                issueResult.discountRate()
        );
    }
}
