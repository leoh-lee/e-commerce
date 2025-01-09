package kr.hhplus.be.server.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);

    Optional<Payment> findById(Long paymentId);

    List<Payment> findByUserId(Long userId);
}
