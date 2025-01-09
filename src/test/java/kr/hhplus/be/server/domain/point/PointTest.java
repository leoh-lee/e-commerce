package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.point.exception.PointLimitExceededException;
import kr.hhplus.be.server.domain.point.exception.PointNotEnoughException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PointTest {

    @Test
    @DisplayName("잔고가 사용하려는 포인트가 적은 경우 예외가 발생한다.")
    void usePoint_whenNotEnoughPoint_throwsPointNotEnoughException() {
        // given
        int amount = 101_000;
        int balance = 100_000;
        long userId = 1L;

        User user = new User(userId, "testUser");
        Point point = new Point(1L, user, balance);

        // when // then
        assertThatThrownBy(() -> point.usePoint(amount))
                .isInstanceOf(PointNotEnoughException.class);
    }

    @Test
    @DisplayName("포인트를 사용(차감)한다.")
    void usePoint_success() {
        // given
        int amount = 90_000;
        int balance = 100_000;
        long userId = 1L;

        User user = new User(userId, "testUser");
        Point point = new Point(1L, user, balance);

        // when
        point.usePoint(amount);

        // then
        assertThat(point.getBalance()).isEqualTo(balance - amount);
    }

    @Test
    @DisplayName("포인트 충전 후 2,000,000 이상이면 예외가 발생한다.")
    void chargePoint_whenLimitExceeded_throwsPointLimitExceededException() {
        // given
        int balance = 1_900_000;
        int chargeAmount = 200_000;
        long userId = 1L;

        User user = new User(userId, "testUser");
        Point point = new Point(1L, user, balance);

        // when // then
        assertThatThrownBy(() -> point.chargePoint(chargeAmount))
                .isInstanceOf(PointLimitExceededException.class);
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void chargePoint_success() {
        // given
        int balance = 1_900_000;
        int chargeAmount = 100_000;
        long userId = 1L;

        User user = new User(userId, "testUser");
        Point point = new Point(1L, user, balance);

        // when
        point.chargePoint(chargeAmount);

        // then
        assertThat(point.getBalance()).isEqualTo(balance + chargeAmount);
    }

}
