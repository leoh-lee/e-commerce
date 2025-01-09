package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotUsableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class UserCouponTest {

    LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("사용자의 쿠폰의 만료일이 지났다면 사용할 수 없다.")
    void isUsable_whenExpired_thenFalse() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 1L, UserCouponStatus.ISSUED, now.minusDays(1), null);

        // when
        boolean usable = userCoupon.isUsable(now);

        // then
        assertThat(usable).isFalse();
    }

    @Test
    @DisplayName("사용자의 쿠폰의 발급 상태가 아니라면 사용할 수 없다.")
    void isUsable_whenNotIssuedStatus_thenFalse() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 1L, UserCouponStatus.EXPIRED, now.plusDays(1), null);

        // when
        boolean usable = userCoupon.isUsable(now);

        // then
        assertThat(usable).isFalse();
    }

    @Test
    @DisplayName("사용자의 쿠폰의 발급 상태이고 만료일 전이라면 사용할 수 있다.")
    void isUsable() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 1L, UserCouponStatus.ISSUED, now.plusDays(1), null);

        // when
        boolean usable = userCoupon.isUsable(now);

        // then
        assertThat(usable).isTrue();
    }

    @ParameterizedTest
    @EnumSource(mode = EXCLUDE, names = {"ISSUED"})
    @DisplayName("사용자의 쿠폰을 사용처리 할 때, 발급 상태가 아니라면 예외가 발생한다.")
    void changeUseStatus_whenNotIssued_throwCouponNotUsableException(UserCouponStatus userCouponStatus) {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 1L, userCouponStatus, now, null);

        // when
        // then
        assertThatThrownBy(() -> userCoupon.changeUseStatus(now))
                .isInstanceOf(CouponNotUsableException.class);
    }

    @Test
    @DisplayName("사용자의 쿠폰을 사용처리할 때 쿠폰 상태가 USED로 변경되고, useDate가 갱신된다.")
    void changeUseStatus() {
        // given
        UserCoupon userCoupon = new UserCoupon(1L, 1L, UserCouponStatus.ISSUED, now, null);

        // when
        userCoupon.changeUseStatus(now);

        // then
        assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);
        assertThat(userCoupon.getUseDate().isEqual(now)).isTrue();
    }

}
