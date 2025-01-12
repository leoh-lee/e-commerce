package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.supoort.RepositoryTest;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponDto;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class UserCouponRepositoryTest extends RepositoryTest {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test1");
        em.persist(user);
        em.flush();
    }

    @Test
    @DisplayName("유저 ID로 사용자 쿠폰 목록을 조회한다.")
    void findByUserId() {
        // given
        Coupon coupon1 = createAndSaveCoupon("10% 할인쿠폰", CouponType.PERCENTAGE);
        Coupon coupon2 = createAndSaveCoupon("10,000원 할인쿠폰", CouponType.FIXED);
        Coupon coupon3 = createAndSaveCoupon("50% 할인쿠폰", CouponType.PERCENTAGE);

        UserCoupon userCoupon1 = new UserCoupon(user.getId(), coupon1.getId(), UserCouponStatus.ISSUED, LocalDateTime.now(), null);
        UserCoupon userCoupon2 = new UserCoupon(user.getId(), coupon2.getId(), UserCouponStatus.ISSUED, LocalDateTime.now(), null);
        userCouponRepository.save(userCoupon1);
        userCouponRepository.save(userCoupon2);

        // when
        List<UserCoupon> findUserCoupons = userCouponRepository.findByUserId(user.getId());

        // then
        assertThat(findUserCoupons).hasSize(2);
    }

    @Test
    @DisplayName("사용자 ID와 쿠폰 ID로 사용자 쿠폰을 조회한다.")
    void findByUserIdAndCouponId() {
        // given
        Coupon coupon = createAndSaveCoupon("10% 할인쿠폰", CouponType.PERCENTAGE);

        UserCoupon userCoupon = new UserCoupon(user.getId(), coupon.getId(), UserCouponStatus.ISSUED, LocalDateTime.now(), null);
        userCouponRepository.save(userCoupon);
        em.flush();

        // when
        UserCoupon findUserCoupon = userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());

        // then
        assertThat(findUserCoupon).extracting("userId", "couponId").containsExactly(user.getId(), coupon.getId());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 쿠폰 목록을 조회한다.")
    void findByUserIdWithCoupon() {
        // given
        Coupon coupon1 = createAndSaveCoupon("10% 할인쿠폰", CouponType.PERCENTAGE);
        Coupon coupon2 = createAndSaveCoupon("10,000원 할인쿠폰", CouponType.FIXED);

        Long userId = user.getId();
        UserCoupon userCoupon1 = new UserCoupon(userId, coupon1.getId(), UserCouponStatus.ISSUED, LocalDateTime.now(), null);
        UserCoupon userCoupon2 = new UserCoupon(userId, coupon2.getId(), UserCouponStatus.ISSUED, LocalDateTime.now(), null);
        userCouponRepository.save(userCoupon1);
        userCouponRepository.save(userCoupon2);

        em.flush();

        // when
        List<UserCouponDto> userCouponDtos = userCouponRepository.findByUserIdWithCoupon(userId);

        // then
        assertThat(userCouponDtos).extracting("userCoupon", "coupon")
                .containsExactly(
                        tuple(userCoupon1, coupon1),
                        tuple(userCoupon2, coupon2)
                );
    }

    @Test
    @DisplayName("사용자 쿠폰 ID로 사용자 쿠폰을 단 건 조회한다.")
    void findByIdWithCoupon() {
        // given
        Coupon coupon = createAndSaveCoupon("10% 할인쿠폰", CouponType.PERCENTAGE);

        Long userId = user.getId();
        UserCoupon userCoupon = new UserCoupon(userId, coupon.getId(), UserCouponStatus.ISSUED, LocalDateTime.now(), null);

        userCouponRepository.save(userCoupon);
        em.flush();

        // when
        UserCouponDto userCouponDto = userCouponRepository.findByIdWithCoupon(userCoupon.getUserId());

        // then
        assertThat(userCouponDto).extracting("userCoupon", "coupon")
                .containsExactly(userCoupon, coupon);
    }

    private Coupon createAndSaveCoupon(String couponName, CouponType couponType) {
        CouponInfo couponInfo = new CouponInfo(couponName, couponType, 10);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        Coupon coupon = new Coupon(couponInfo, discountInfo, couponUsableDate);

        em.persist(coupon);
        em.flush();

        return coupon;
    }


}
