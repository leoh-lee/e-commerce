package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

public class CouponFacadeConcurrencyTest extends IntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    private Long couponId;
    private Long userId;

    @BeforeEach
    void setUp() {
        CouponInfo couponInfo = new CouponInfo("10% 할인 쿠폰", CouponType.FIXED, 5);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        Coupon coupon =  new Coupon(couponInfo, discountInfo, couponUsableDate);
        couponRepository.save(coupon);
        couponId = coupon.getId();

        User user = new User("user1");
        userRepository.save(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("한 명의 사용자가 여러 번 쿠폰을 발급받은 경우, 한 번만 발급된다.")
    void issueCoupon_concurrentRequests_shouldMaintainDataIntegrity() throws InterruptedException {
        // given
        int numberOfThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(userId, couponId);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    CouponIssueResponse result = couponFacade.issueCoupon(couponIssueRequest);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // then
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getCouponInfo().getCouponStock()).isEqualTo(4);
        long issuedCoupons = userCouponRepository.findByUserId(userId).size();
        assertThat(issuedCoupons).isEqualTo(1);
    }

}
