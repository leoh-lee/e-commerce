package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;

public record UserCouponDto(
        UserCoupon userCoupon,
        Coupon coupon
) {
}
