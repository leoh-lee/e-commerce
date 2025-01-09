package kr.hhplus.be.server.infrastructures.core.coupon;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    @Query("select uc from UserCoupon uc where uc.userId = :userId")
    List<UserCoupon> findByUserId(@Param("userId") Long userId);

    @Query("select uc from UserCoupon uc where uc.userId = :userId and uc.couponId = :couponId")
    UserCoupon findByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);

    @Query("select new kr.hhplus.be.server.domain.coupon.dto.UserCouponDto(uc, c)" +
            "from UserCoupon uc " +
            "join Coupon c " +
            "on uc.couponId = c.id " +
            "where uc.userId = :userId")
    List<UserCouponDto> findByUserIdWithCoupon(@Param("userId") Long userId);

    @Query("select new kr.hhplus.be.server.domain.coupon.dto.UserCouponDto(uc, c)" +
            "from UserCoupon uc " +
            "join Coupon c " +
            "on uc.couponId = c.id " +
            "where uc.id = :id")
    UserCouponDto findByIdWithCoupon(@Param("id") Long id);
}
