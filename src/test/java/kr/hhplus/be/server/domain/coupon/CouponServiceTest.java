package kr.hhplus.be.server.domain.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.coupon.dto.CouponSearchResult;
import kr.hhplus.be.server.domain.coupon.dto.CouponUseResult;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponDto;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponSearchResult;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.exception.AlreadyIssuedCouponException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.support.util.DateTimeProvider;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("사용자가 발급 가능한 쿠폰 목록을 조회한다.")
    void getIssuableCoupons_success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        when(dateTimeProvider.getLocalDateTimeNow()).thenReturn(now);

        String couponName = "10% 할인 쿠폰";

        Coupon coupon1 = createCoupon(1L, couponName, CouponType.PERCENTAGE);
        Coupon coupon2 = createCoupon(2L, "10,000원 할인 쿠폰", CouponType.FIXED);

        List<Coupon> coupons = List.of(
                coupon1,
                coupon2
        );

        long userId = 1L;

        UserCoupon userCoupon = new UserCoupon(userId, coupon1.getId(), UserCouponStatus.ISSUED, now.plusDays(5), null);

        List<UserCoupon> userCoupons = List.of(
                userCoupon
        );

        when(couponRepository.findAll()).thenReturn(coupons);
        when(userCouponRepository.findByUserId(userId)).thenReturn(userCoupons);

        // when
        List<CouponSearchResult> issuableCoupons = couponService.getIssuableCoupons(userId);

        // then
        assertThat(issuableCoupons).hasSize(1);
        assertThat(issuableCoupons.getFirst().id()).isEqualTo(coupon2.getId());
    }

    @Test
    @DisplayName("쿠폰 단 건 조회 시 해당하는 쿠폰이 없으면 예외가 발생한다.")
    void getCouponById_whenNotFound_throwsCouponNotFoundException() {
        // given
        long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> couponService.getCouponById(couponId))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    @DisplayName("쿠폰 단 건 조회에 성공한다.")
    void getCouponById_success() {
        // given
        long couponId = 1L;
        String couponName = "10% 할인 쿠폰";
        CouponInfo couponInfo = new CouponInfo(couponName, CouponType.PERCENTAGE, 10);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);

        Coupon coupon = new Coupon(couponInfo, discountInfo, null);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        CouponSearchResult result = couponService.getCouponById(couponId);

        // then
        assertThat(result.couponName()).isEqualTo(couponName);
    }

    @Test
    @DisplayName("사용자 쿠폰을 발급할 때 해당 쿠폰이 존재하지 않으면 예외가 발생한다.")
    void issueCoupon_whenCouponNotFound_throwsCouponNotFoundException() {
        // given
        long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> couponService.issueCoupon(1L, couponId))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 쿠폰을 발급하면 쿠폰의 재고가 하나 감소한다.")
    void issueCoupon_thenCouponStockDecreased() {
        // given
        long couponId = 1L;
        Coupon coupon = createCoupon(couponId, "10% 할인쿠폰", CouponType.PERCENTAGE);
        int originalCouponStock = coupon.getCouponInfo().getCouponStock();
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(dateTimeProvider.getLocalDateTimeNow()).thenReturn(LocalDateTime.now());

        // when
        couponService.issueCoupon(1L, couponId);

        // then
        assertThat(coupon.getCouponInfo().getCouponStock()).isEqualTo(originalCouponStock - 1);
    }

    @Test
    @DisplayName("사용자 쿠폰을 발급할 때, 이미 발급한 쿠폰이면 예외가 발생한다.")
    void issueCoupon_whenAlreadyIssuedCoupon_throwsAlreadyIssuedCouponException() {
        // given
        long couponId = 1L;
        Coupon coupon = createCoupon(couponId,"10% 할인쿠폰", CouponType.PERCENTAGE);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(dateTimeProvider.getLocalDateTimeNow()).thenReturn(LocalDateTime.now());
        doThrow(new AlreadyIssuedCouponException()).when(userCouponRepository).save(any());

        // when
        // then
        assertThatThrownBy(() -> couponService.issueCoupon(1L, couponId))
                .isInstanceOf(AlreadyIssuedCouponException.class);
    }

    @Test
    @DisplayName("사용자 쿠폰을 사용한다.")
    void useCoupon() {
        // given
        LocalDateTime now = LocalDateTime.now();

        long couponId = 1L;

        UserCoupon userCoupon = new UserCoupon(1L, couponId, UserCouponStatus.ISSUED, now, null);
        UserCouponDto userCouponDto = new UserCouponDto(userCoupon, createCoupon(couponId, "10% 할인 쿠폰", CouponType.PERCENTAGE));

        when(dateTimeProvider.getLocalDateTimeNow()).thenReturn(now);
        when(userCouponRepository.findByIdWithCoupon(1L)).thenReturn(userCouponDto);

        // when
        CouponUseResult couponUseResult = couponService.useCoupon(1L, BigDecimal.valueOf(10000));

        // then
        assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);
        assertThat(userCoupon.getUseDate().isEqual(now)).isTrue();
        assertThat(couponUseResult).extracting("couponId", "userId").containsExactly(1L, 1L);
    }
    
    @Test
    @DisplayName("사용자 쿠폰을 조회한다.")
    void getUserCoupons() {
        // given
        long userId = 1L;
        long couponId1 = 1L;
        long couponId2 = 2L;
        Coupon coupon1 = createCoupon(couponId1, "10% 할인쿠폰", CouponType.PERCENTAGE);
        Coupon coupon2 = createCoupon(couponId2, "10,000원 할인쿠폰", CouponType.FIXED);

        UserCoupon userCoupon1 = new UserCoupon(userId, 1L, UserCouponStatus.ISSUED, LocalDateTime.now(), null);
        UserCoupon userCoupon2 = new UserCoupon(userId, 2L, UserCouponStatus.USED, LocalDateTime.now(), null);

        List<UserCouponDto> userCouponDto = List.of(
                new UserCouponDto(userCoupon1, coupon1),
                new UserCouponDto(userCoupon2, coupon2)
        );

        when(userCouponRepository.findByUserIdWithCoupon(userId)).thenReturn(userCouponDto);

        // when
        List<UserCouponSearchResult> userCoupons = couponService.getUserCoupons(userId);

        // then
        assertThat(userCoupons)
                .extracting("userId", "userCouponStatus")
                .containsExactly(
                        tuple(userId, UserCouponStatus.ISSUED),
                        tuple(userId, UserCouponStatus.USED)
                );
    }

    private Coupon createCoupon(Long couponId, String couponName, CouponType couponType) {
        CouponInfo couponInfo = new CouponInfo(couponName, couponType, 10);
        DiscountInfo discountInfo = new DiscountInfo(null, 10);
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));

        return new Coupon(couponId, couponInfo, discountInfo, couponUsableDate);
    }
}