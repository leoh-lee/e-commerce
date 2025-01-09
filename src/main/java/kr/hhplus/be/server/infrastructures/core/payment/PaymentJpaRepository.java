package kr.hhplus.be.server.infrastructures.core.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p join Order o on p.orderId = o.id where o.userId = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);
}
