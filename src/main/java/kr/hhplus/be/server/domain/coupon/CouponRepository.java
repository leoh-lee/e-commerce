package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

    List<Coupon> findAll();

    Optional<Coupon> findById(Long couponId);

    Optional<Coupon> findByIdWithLock(Long couponId);

    void save(Coupon coupon);
}
