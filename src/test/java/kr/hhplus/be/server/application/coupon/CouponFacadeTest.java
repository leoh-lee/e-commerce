package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructures.core.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructures.core.coupon.UserCouponJpaRepository;
import kr.hhplus.be.server.infrastructures.core.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class CouponFacadeTest extends IntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserCouponJpaRepository userCouponJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @AfterEach
    void tearDown() {
        userCouponJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    void issueCoupon_success() {
        // given
        Coupon coupon = createAndSaveCoupon("쿠폰1", CouponType.PERCENTAGE);

        int originalStock = coupon.getCouponInfo().getCouponStock();
        Long couponId = coupon.getId();

        User testUser = new User("test user");
        userRepository.save(testUser);
        Long userId = testUser.getId();

        CouponIssueRequest request = new CouponIssueRequest(userId, couponId);

        // when
        CouponIssueResponse response = couponFacade.issueCoupon(request);

        // then
        assertThat(response.id()).isEqualTo(couponId);

        Coupon findCoupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(findCoupon.getCouponInfo().getCouponStock()).isEqualTo(originalStock - 1);

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        assertThat(userCoupons).hasSize(1);
        assertThat(userCoupons.getFirst().getUserCouponStatus()).isEqualTo(UserCouponStatus.ISSUED);
    }

    @Test
    void getUserCoupons_success() {
        // given
        Coupon coupon = createAndSaveCoupon("쿠폰", CouponType.PERCENTAGE);
        Long couponId = coupon.getId();

        User user = new User("user");
        userRepository.save(user);
        Long userId = user.getId();

        UserCoupon userCoupon = new UserCoupon(userId, couponId, UserCouponStatus.ISSUED, null, null);

        userCouponRepository.save(userCoupon);

        // when
        List<UserCouponSearchResponse> responses = couponFacade.getUserCoupons(userId);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(userCoupon.getId());
        assertThat(responses.getFirst().type()).isEqualTo(UserCouponStatus.ISSUED.name());
    }

    @Test
    void getIssuableCoupons_success() {
        // given
        List<Long> couponIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Coupon coupon = createAndSaveCoupon("쿠폰" + i, CouponType.PERCENTAGE);
            couponIds.add(coupon.getId());
        }

        User user = new User("user");
        userRepository.save(user);
        Long userId = user.getId();

        userCouponRepository.save(new UserCoupon(userId, couponIds.getFirst(), UserCouponStatus.ISSUED, null, null));

        // when
        List<AvailableCouponResponse> responses = couponFacade.getIssuableCoupons(userId);

        // then
        assertThat(responses).hasSize(9);
    }

    private Coupon createAndSaveCoupon(String couponName, CouponType couponType) {
        CouponInfo couponInfo = new CouponInfo(couponName, couponType, 10);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        Coupon coupon = new Coupon(couponInfo, discountInfo, couponUsableDate);

        couponRepository.save(coupon);

        return coupon;
    }
}
