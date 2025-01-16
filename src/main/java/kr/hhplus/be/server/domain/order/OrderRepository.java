package kr.hhplus.be.server.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findById(Long orderId);
}
