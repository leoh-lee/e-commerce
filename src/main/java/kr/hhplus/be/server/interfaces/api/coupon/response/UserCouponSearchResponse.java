package kr.hhplus.be.server.interfaces.api.coupon.response;

import kr.hhplus.be.server.domain.coupon.dto.CouponSearchResult;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponSearchResult;

public record UserCouponSearchResponse(
        Long id,
        String name,
        String type,
        Integer discountAmount,
        Integer discountRate
) {

    public static UserCouponSearchResponse from(UserCouponSearchResult searchResult) {
        CouponSearchResult coupon = searchResult.coupon();
        return new UserCouponSearchResponse(searchResult.id(), coupon.couponName(), searchResult.userCouponStatus().name(), coupon.discountAmount(), coupon.discountRate());
    }

}
