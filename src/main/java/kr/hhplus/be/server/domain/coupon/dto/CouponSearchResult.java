package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.DiscountInfo;

public record CouponSearchResult(
        Long id,
        String couponName,
        CouponType couponType,
        int couponStock,
        Integer discountAmount,
        Integer discountRate
) {
    public static CouponSearchResult fromEntity(Coupon coupon) {
        CouponInfo couponInfo = coupon.getCouponInfo();
        DiscountInfo discountInfo = coupon.getDiscountInfo();

        return new CouponSearchResult(coupon.getId(), couponInfo.getCouponName(), couponInfo.getCouponType(), couponInfo.getCouponStock(), discountInfo.getDiscountAmount(), discountInfo.getDiscountRate());
    }
}
