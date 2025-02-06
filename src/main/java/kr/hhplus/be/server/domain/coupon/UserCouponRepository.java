package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.UserCouponDto;

import java.util.List;

public interface UserCouponRepository {
    List<UserCoupon> findByUserId(Long userId);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCouponDto> findByUserIdWithCoupon(Long userId);

    UserCouponDto findByIdWithCoupon(Long userCouponId);

    void save(UserCoupon userCoupon);

    void saveAll(List<UserCoupon> userCoupons);
}
