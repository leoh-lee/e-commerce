package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderPrice;
import kr.hhplus.be.server.infrastructures.core.payment.PaymentRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PaymentRepositoryImpl.class)
class PaymentRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("사용자 ID로 결제 목록을 조회한다.")
    void findByUserId() {
        // given
        Long userId = 1L;

        OrderPrice orderPrice = new OrderPrice(20000, 10000, 10000);

        Order order1 = new Order(1L, 1L, orderPrice);
        Order order2 = new Order(1L, 2L, orderPrice);

        em.persist(order1);
        em.persist(order2);
        em.flush();

        Payment payment1 = new Payment(order1.getId(), 10_000, PaymentStatus.PENDING);
        paymentRepository.save(payment1);

        Payment payment2 = new Payment(order2.getId(), 20_000, PaymentStatus.COMPLETED);
        paymentRepository.save(payment2);

        em.flush();

        // when
        List<Payment> payments = paymentRepository.findByUserId(userId);

        // then
        assertThat(payments)
                .extracting("orderId", "paymentPrice", "paymentStatus")
                .containsExactly(
                        tuple(order1.getId(), 10_000, PaymentStatus.PENDING),
                        tuple(order2.getId(), 20_000, PaymentStatus.COMPLETED)
                );
    }

}