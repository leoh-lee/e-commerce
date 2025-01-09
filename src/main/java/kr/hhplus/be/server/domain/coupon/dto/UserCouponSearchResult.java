package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;

import java.time.LocalDateTime;

public record UserCouponSearchResult(
        Long id,
        Long userId,
        CouponSearchResult coupon,
        UserCouponStatus userCouponStatus,
        LocalDateTime expiredDate,
        LocalDateTime useDate
) {

    public static UserCouponSearchResult from(UserCouponDto userCouponDto) {
        UserCoupon userCoupon = userCouponDto.userCoupon();
        return new UserCouponSearchResult(
                userCoupon.getId(),
                userCoupon.getUserId(),
                CouponSearchResult.fromEntity(userCouponDto.coupon()),
                userCoupon.getUserCouponStatus(),
                userCoupon.getExpiredDate(),
                userCoupon.getUseDate()
        );
    }
}
