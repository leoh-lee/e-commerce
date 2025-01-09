package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class CouponFacadeTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    private Long couponId;

    @BeforeEach
    void setUp() {
        CouponInfo couponInfo = new CouponInfo("10% 할인 쿠폰", CouponType.FIXED, 5);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        Coupon coupon =  new Coupon(couponId, couponInfo, discountInfo, couponUsableDate);
        couponRepository.save(coupon);
        couponId = coupon.getId();
    }

    @Test
    @Transactional
    void issueCoupon_success() {
        // given
        User testUser = new User("test user");
        userRepository.save(testUser);
        Long userId = testUser.getId();

        CouponIssueRequest request = new CouponIssueRequest(userId, couponId);

        // when
        CouponIssueResponse response = couponFacade.issueCoupon(request);

        // then
        assertThat(response.id()).isEqualTo(couponId);

        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getCouponInfo().getCouponStock()).isEqualTo(4);

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        assertThat(userCoupons).hasSize(1);
        assertThat(userCoupons.getFirst().getUserCouponStatus()).isEqualTo(UserCouponStatus.ISSUED);
    }

    @Test
    @Transactional
    void getUserCoupons_success() {
        // given
        Long userId = 1L;
        userCouponRepository.save(new UserCoupon(userId, couponId, UserCouponStatus.ISSUED, null, null));

        // when
        List<UserCouponSearchResponse> responses = couponFacade.getUserCoupons(userId);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(couponId);
        assertThat(responses.getFirst().type()).isEqualTo(UserCouponStatus.ISSUED.name());
    }
}
