package kr.hhplus.be.server.infrastructures.core.coupon;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return userCouponJpaRepository.findByUserId(userId);
    }

    @Override
    public UserCoupon findByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public void save(UserCoupon userCoupon) {
        userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public List<UserCouponDto> findByUserIdWithCoupon(Long userId) {
        return userCouponJpaRepository.findByUserIdWithCoupon(userId);
    }

    @Override
    public UserCouponDto findByIdWithCoupon(Long userCouponId) {
        return userCouponJpaRepository.findByIdWithCoupon(userCouponId);
    }
}
