package kr.hhplus.be.server.interfaces.api.coupon.response;

import kr.hhplus.be.server.domain.coupon.dto.CouponSearchResult;

public record AvailableCouponResponse(
        Long id,
        String name,
        String type,
        Integer discountAmount,
        Integer discountRate
) {

    public static AvailableCouponResponse from(CouponSearchResult couponSearchResult) {
        return new AvailableCouponResponse(couponSearchResult.id(), couponSearchResult.couponName(), couponSearchResult.couponType().name(), couponSearchResult.discountAmount(), couponSearchResult.discountRate());
    }

}
