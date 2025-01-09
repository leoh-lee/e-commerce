package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.infrastructures.core.order.OrderRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(OrderRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("사용자 ID로 주문 목록을 조회한다.")
    void findByUserId() {
        // given
        long userId = 1L;
        long otherUserId = 2L;

        OrderPrice orderPrice = new OrderPrice(20_000, 10_000, 10_000);
        Order order1 = new Order(userId, 1L, orderPrice);
        Order order2 = new Order(userId, 2L, orderPrice);
        Order order3 = new Order(otherUserId, 2L, orderPrice);

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        // then
        assertThat(orders.getContent()).hasSize(2);
    }

}