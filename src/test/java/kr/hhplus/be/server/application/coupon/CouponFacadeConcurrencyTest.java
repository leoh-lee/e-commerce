package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructures.core.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructures.core.coupon.UserCouponJpaRepository;
import kr.hhplus.be.server.infrastructures.core.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class CouponFacadeConcurrencyTest {

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

    private Long couponId;
    private Long userId;

    @BeforeEach
    void setUp() {
        CouponInfo couponInfo = new CouponInfo("10% 할인 쿠폰", CouponType.FIXED, 5);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        Coupon coupon =  new Coupon(couponId, couponInfo, discountInfo, couponUsableDate);
        couponRepository.save(coupon);
        couponId = coupon.getId();

        User user = new User("user1");
        userRepository.save(user);
        userId = user.getId();
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
        userCouponJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("한 명의 사용자가 여러 번 쿠폰을 발급받은 경우, 한 번만 발급된다.")
    void issueCoupon_concurrentRequests_shouldMaintainDataIntegrity() throws InterruptedException {
        // given
        int numberOfThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        List<CouponIssueResponse> results = Collections.synchronizedList(new ArrayList<>());
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(userId, couponId);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    barrier.await(); // 모든 스레드가 동시에 시작되도록 대기
                    CouponIssueResponse result = couponFacade.issueCoupon(couponIssueRequest);
                    results.add(result);
                } catch (Exception e) {
                    exceptions.add(e);
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
        assertThat(results.size()).isEqualTo(1);    // 유저 한 명은 동일한 쿠폰을 한 번 밖에 발급 받지 못함.
        assertThat(exceptions.size()).isEqualTo(9);

        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getCouponInfo().getCouponStock()).isEqualTo(4);
        long issuedCoupons = userCouponRepository.findByUserId(userId).size();
        assertThat(issuedCoupons).isEqualTo(1);
    }

    @Test
    @DisplayName("한 명의 사용자가 여러 번 쿠폰을 발급받은 경우, 한 번만 발급된다.")
    void issueCoupon_multipleUsers_concurrentRequests_shouldMaintainDataIntegrity() throws InterruptedException {
        // given
        int userCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch latch = new CountDownLatch(userCount);
        CyclicBarrier barrier = new CyclicBarrier(userCount);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        List<CouponIssueResponse> results = Collections.synchronizedList(new ArrayList<>());

        List<Long> userIds = new ArrayList<>();

        for (int i = 0; i < userCount; i++) {
            final Long userId = (long) i + 1;
            User user = new User("user" + userId);
            userRepository.save(user);

            userIds.add(user.getId());
        }

        for (int i = 0; i < userIds.size(); i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    barrier.await();
                    CouponIssueRequest couponIssueRequest = new CouponIssueRequest(userIds.get(finalI), couponId);
                    CouponIssueResponse result = couponFacade.issueCoupon(couponIssueRequest);
                    results.add(result);
                } catch (Exception e) {
                    exceptions.add(e);
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

        assertThat(results.size()).isEqualTo(5);    // 유저 한 명은 동일한 쿠폰을 한 번 밖에 발급 받지 못함.
        assertThat(exceptions.size()).isEqualTo(userCount - 5);

        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getCouponInfo().getCouponStock()).isEqualTo(0);
    }

}
