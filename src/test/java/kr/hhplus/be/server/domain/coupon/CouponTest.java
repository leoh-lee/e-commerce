package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.exception.CouponStockNotEnoughException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("쿠폰 사용기간인 지 확인한다.")
    void isUsable() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CouponUsableDate couponUsableDate = new CouponUsableDate(now.minusDays(1), now.plusDays(1));
        CouponUsableDate couponDisableDate = new CouponUsableDate(now.minusDays(5), now.minusDays(1));

        Coupon usableCoupon = new Coupon(null, null, couponUsableDate);
        Coupon disableCoupon = new Coupon(null, null, couponDisableDate);

        // when
        boolean usable = usableCoupon.isUsable(LocalDateTime.now());
        boolean disabled = disableCoupon.isUsable(LocalDateTime.now());

        // then
        assertThat(usable).isTrue();
        assertThat(disabled).isFalse();
    }

    @Test
    @DisplayName("쿠폰의 재고를 감소시킨다.")
    void decreaseStock() {
        // given
        CouponInfo couponInfo = new CouponInfo("10% 할인쿠폰", CouponType.PERCENTAGE, 10);
        Coupon coupon = new Coupon(couponInfo, null, null);

        for (int i = 0; i < 10; i++) {
            coupon.decreaseStock();
        }

        // when
        // then
        assertThatThrownBy(coupon::decreaseStock)
                .isInstanceOf(CouponStockNotEnoughException.class);
    }

    @Test
    @DisplayName("정액 할인 금액 조회 시 할인 금액이 가격보다 크면 가격이 반환된다.")
    void getDiscountPrice_whenDiscountAmountGreaterThanPrice_returnPrice() {
        // given
        CouponInfo couponInfo = new CouponInfo("10,000원 할인쿠폰", CouponType.FIXED, 10);
        DiscountInfo discountInfo = new DiscountInfo(10_000, null);
        Coupon coupon = new Coupon(couponInfo, discountInfo, null);
        BigDecimal price = BigDecimal.valueOf(9_900);

        // when
        BigDecimal discountPrice = coupon.getDiscountPrice(price);

        // then
        assertThat(discountPrice).isEqualByComparingTo(price);
    }

    @ParameterizedTest
    @CsvSource({"100000, 10000, 10000", "5000, 3000, 3000", "50000, 35000, 35000", "30000, 40000, 30000"})
    @DisplayName("정액 할인 금액을 반환한다.")
    void getDiscountPrice_whenFixedType(int price, int discountAmount, int result) {
        // given
        CouponInfo couponInfo = new CouponInfo("10,000원 할인쿠폰", CouponType.FIXED, 10);
        DiscountInfo discountInfo = new DiscountInfo(discountAmount, null);
        Coupon coupon = new Coupon(couponInfo, discountInfo, null);

        // when
        BigDecimal discountedPrice = coupon.getDiscountPrice(BigDecimal.valueOf(price));

        // then
        assertThat(discountedPrice).isEqualByComparingTo(BigDecimal.valueOf(result));
    }

    @ParameterizedTest
    @CsvSource({"100000, 10, 10000", "5000, 50, 2500", "50000, 15, 7500"})
    @DisplayName("정률 할인 금액을 반환한다.")
    void getDiscountPrice_whenPercentageType(int price, int discountRate, int result) {
        // given
        CouponInfo couponInfo = new CouponInfo(discountRate +"% 할인쿠폰", CouponType.PERCENTAGE, 10);
        DiscountInfo discountInfo = new DiscountInfo(null, discountRate);
        Coupon coupon = new Coupon(couponInfo, discountInfo, null);

        // when
        BigDecimal discountedPrice = coupon.getDiscountPrice(BigDecimal.valueOf(price));

        // then
        assertThat(discountedPrice).isEqualByComparingTo(BigDecimal.valueOf(result));
    }

}