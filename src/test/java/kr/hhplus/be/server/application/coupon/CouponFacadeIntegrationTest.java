package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.TestDataBuilder;
import kr.hhplus.be.server.support.TestDataPlatform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CouponFacadeIntegrationTest extends IntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataPlatform dataPlatform;

    @Nested
    @DisplayName("쿠폰 발급")
    class IssueCoupon {
        @Test
        @DisplayName("쿠폰을 발급할 때 사용자가 존재하지 않으면 실패한다.")
        void issueCoupon_whenUserNotExists_throwsUserNotFoundException() {
            // given
            Long nonExistsUserId = 999999L;
            Coupon coupon = testDataBuilder.createCoupon("10% 할인 쿠폰", CouponType.PERCENTAGE, 100, 0, 10);

            CouponIssueRequest couponIssueRequest = new CouponIssueRequest(nonExistsUserId, coupon.getId());

            // when
            // then
            assertThatThrownBy(() -> couponFacade.issueCoupon(couponIssueRequest))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @ParameterizedTest
        @CsvSource({"10, 9", "100, 99", "33, 32"})
        @DisplayName("쿠폰을 발급하면 해당 쿠폰의 재고가 하나 감소한다.")
        void issueCoupon_whenSuccess_thenCouponStockDecreased(int originStock, int result) {
            // given
            User user = testDataBuilder.createUser("test");
            Coupon coupon = testDataBuilder.createCoupon("10% 할인 쿠폰", CouponType.PERCENTAGE, originStock, 0, 10);

            CouponIssueRequest couponIssueRequest = new CouponIssueRequest(user.getId(), coupon.getId());

            // when
            couponFacade.issueCoupon(couponIssueRequest);

            // then
            Coupon findCoupon = couponRepository.findById(coupon.getId()).get();
            assertThat(findCoupon.getCouponInfo().getCouponStock()).isEqualTo(result);
        }

        @Test
        @DisplayName("쿠폰을 발급에 성공하면 발급 상태의 사용자 쿠폰이 생성된다.")
        void issueCoupon_whenSuccess_thenUserCouponCreated() {
            // given
            User user = testDataBuilder.createUser("test");
            Coupon coupon = testDataBuilder.createCoupon("10% 할인 쿠폰", CouponType.PERCENTAGE, 10, 0, 10);

            CouponIssueRequest couponIssueRequest = new CouponIssueRequest(user.getId(), coupon.getId());

            // when
            couponFacade.issueCoupon(couponIssueRequest);

            // then
            UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());

            assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.ISSUED);
            assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId());
            assertThat(userCoupon.getUserId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("쿠폰을 발급에 성공하면 데이터 플랫폼에 내역이 전송된다.")
        void issueCoupon_whenSuccess_thenDataPlatformSend() {
            // given
            User user = testDataBuilder.createUser("test");
            Coupon coupon = testDataBuilder.createCoupon("10% 할인 쿠폰", CouponType.PERCENTAGE, 10, 0, 10);

            CouponIssueRequest couponIssueRequest = new CouponIssueRequest(user.getId(), coupon.getId());

            TestDataPlatform testDataPlatform = (TestDataPlatform) dataPlatform;

            Integer originSendCount = testDataPlatform.getSentCount();

            // when
            couponFacade.issueCoupon(couponIssueRequest);

            // then
            assertThat(testDataPlatform.getSentCount()).isEqualTo(originSendCount + 1);
        }
    }

    @Nested
    @DisplayName("사용자 쿠폰 조회")
    class SearchUserCoupon {
        @Test
        @DisplayName("사용자 ID로 사용자 쿠폰을 조회한다.")
        void getUserCoupon_success() {
            // given
            Coupon coupon = testDataBuilder.createCoupon("10% 할인 쿠폰", CouponType.PERCENTAGE, 10, 0, 10);
            User user = testDataBuilder.createUser("test");
            UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon.getId(), LocalDateTime.now().plusDays(5));

            // when
            List<UserCouponSearchResponse> result = couponFacade.getUserCoupons(user.getId());

            // then
            assertThat(result.getFirst()).extracting("name", "status", "discountAmount", "discountRate")
                    .containsExactly(coupon.getCouponInfo().getCouponName(), userCoupon.getUserCouponStatus().name(), coupon.getDiscountInfo().getDiscountAmount(), coupon.getDiscountInfo().getDiscountRate());
        }
    }

    @Nested
    @DisplayName("발급 가능한 쿠폰 조회")
    class SearchIssuableCoupon {
        @Test
        @DisplayName("사용자 ID로 발급 가능한 쿠폰을 조회한다.")
        void getUserCoupon_success() {
            // given
            Coupon coupon1 = testDataBuilder.createCoupon("10% 할인 쿠폰", CouponType.PERCENTAGE, 10, 0, 10);
            Coupon coupon2 = testDataBuilder.createCoupon("10,000원 할인 쿠폰", CouponType.FIXED, 10, 10_000, 0);
            User user = testDataBuilder.createUser("test");
            UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon1.getId(), LocalDateTime.now().plusDays(5));

            // when
            List<AvailableCouponResponse> issuableCoupons = couponFacade.getIssuableCoupons(user.getId());

            // then
            assertThat(issuableCoupons.getFirst()).extracting("name", "type", "discountAmount", "discountRate")
                    .containsExactly(coupon2.getCouponInfo().getCouponName(), coupon2.getCouponInfo().getCouponType().name(), coupon2.getDiscountInfo().getDiscountAmount(), coupon2.getDiscountInfo().getDiscountRate());
        }
    }

}
