package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.enums.CouponType;

public record CouponIssueResult(
        Long couponId,
        Long userId,
        String couponName,
        CouponType couponType,
        Integer discountAmount,
        Integer discountRate
) {
}
