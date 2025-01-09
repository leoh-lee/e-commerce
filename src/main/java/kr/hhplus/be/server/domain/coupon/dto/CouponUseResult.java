package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.UserCoupon;

import java.time.LocalDateTime;

public record CouponUseResult(
        Long couponId,
        Long userId,
        int originalPrice,
        int discountedPrice,
        int finalPrice,
        LocalDateTime useDate
) {

    public static CouponUseResult of(UserCoupon userCoupon, int price, int discountPrice) {
        return new CouponUseResult(userCoupon.getCouponId(), userCoupon.getUserId(), price, discountPrice, price - discountPrice, userCoupon.getUseDate());
    }

}
