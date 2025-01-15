package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.exception.PointNotEnoughException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.support.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class PaymentFacadeIntegrationTest extends IntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("결제 시 사용자가 존재하지 않으면 실패한다.")
    void payment_whenUserNotExists_throwsUserNotFoundException() {
        // given
        Long nonExistsUser = 99999L;
        PaymentRequest paymentRequest = new PaymentRequest(nonExistsUser, 1L);

        // when
        // then
        assertThatThrownBy(() -> paymentFacade.payment(paymentRequest))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("결제에 성공하면 주문 상태가 결제 완료 상태로 변경된다.")
    void payment_whenSuccess_thenOrderStatusUpdated() {
        // given
        User user = testDataBuilder.createUser("test");
        testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(100_000));

        Order order = testDataBuilder.createOrder(user.getId(), null, 10_000);

        // when
        paymentFacade.payment(new PaymentRequest(user.getId(), order.getId()));

        // then
        Order findOrder = orderRepository.findById(order.getId()).get();

        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYED);
    }

    @Test
    @DisplayName("결제 시 잔여 포인트가 결제 금액보다 적으면 실패한다.")
    void payment_whenNotEnoughPoint_thenPointNotEnoughException() {
        // given
        User user = testDataBuilder.createUser("test");
        testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(100_000));

        Order order = testDataBuilder.createOrder(user.getId(), null, 110_000);

        // when
        // then
        assertThatThrownBy(() -> paymentFacade.payment(new PaymentRequest(user.getId(), order.getId())))
                .isInstanceOf(PointNotEnoughException.class);
    }

    @ParameterizedTest
    @CsvSource({"100000, 10000", "500000, 120000", "133000, 14000"})
    @DisplayName("결제에 성공하면 결제 금액만큼 포인트가 차감한다.")
    void payment_whenSuccess_thenPointDecreased(int balance, int price) {
        // given
        User user = testDataBuilder.createUser("test");
        testDataBuilder.createPoint(user.getId(), BigDecimal.valueOf(balance));

        Order order = testDataBuilder.createOrder(user.getId(), null, price);
        PaymentRequest paymentRequest = new PaymentRequest(user.getId(), order.getId());

        // when
        paymentFacade.payment(paymentRequest);

        // then
        Point point = pointRepository.findByUserId(user.getId()).get();
        assertThat(point.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(balance).subtract(BigDecimal.valueOf(price)));
    }

}
