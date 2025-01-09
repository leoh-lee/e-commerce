package kr.hhplus.be.server.infrastructures.core.order;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.userId = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
