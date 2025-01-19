package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.support.RepositoryTest;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderPrice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PaymentRepositoryTest extends RepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("사용자 ID로 결제 목록을 조회한다.")
    void findByUserId() {
        // given
        Long userId = 1L;

        OrderPrice orderPrice = new OrderPrice(BigDecimal.valueOf(20_000), BigDecimal.valueOf(10_000), BigDecimal.valueOf(10_000));

        Order order1 = new Order(1L, 1L, orderPrice, OrderStatus.ORDERED);
        Order order2 = new Order(1L, 2L, orderPrice, OrderStatus.PAYED);

        em.persist(order1);
        em.persist(order2);
        em.flush();

        Payment payment1 = new Payment(order1.getId(), BigDecimal.valueOf(10_000), PaymentStatus.PENDING);
        paymentRepository.save(payment1);

        Payment payment2 = new Payment(order2.getId(), BigDecimal.valueOf(20_000), PaymentStatus.COMPLETED);
        paymentRepository.save(payment2);

        em.flush();

        // when
        List<Payment> payments = paymentRepository.findByUserId(userId);

        // then
        assertThat(payments)
                .extracting("orderId", "paymentPrice", "paymentStatus")
                .containsExactly(
                        tuple(order1.getId(), BigDecimal.valueOf(10_000), PaymentStatus.PENDING),
                        tuple(order2.getId(), BigDecimal.valueOf(20_000), PaymentStatus.COMPLETED)
                );
    }

}