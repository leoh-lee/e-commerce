package kr.hhplus.be.server.infrastructures.core.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Query("select c from Coupon c join UserCoupon uc on c.id = uc.couponId")
    List<Coupon> findByUserCouponId(Long userId);
}
