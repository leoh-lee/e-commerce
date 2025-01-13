package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.UserCoupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponUseResult(
        Long couponId,
        Long userId,
        BigDecimal originalPrice,
        BigDecimal discountedPrice,
        BigDecimal finalPrice,
        LocalDateTime useDate
) {

    public static CouponUseResult of(UserCoupon userCoupon, BigDecimal price, BigDecimal discountPrice) {
        return new CouponUseResult(userCoupon.getCouponId(), userCoupon.getUserId(), price, discountPrice, price.subtract(discountPrice), userCoupon.getUseDate());
    }

}
