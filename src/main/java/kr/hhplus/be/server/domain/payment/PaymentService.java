package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.payment.dto.PaymentSearchResult;
import kr.hhplus.be.server.domain.payment.exception.PaymentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResult save(Long orderId, BigDecimal price) {
        Payment payment = new Payment(orderId, price, PaymentStatus.PENDING);

        paymentRepository.save(payment);

        return PaymentResult.fromEntity(payment);
    }

    @Transactional
    public void changePaymentStatus(Long paymentId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
        payment.changePaymentStatus(paymentStatus);
    }

    public List<PaymentSearchResult> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);

        return payments.stream()
                .map(payment -> PaymentSearchResult.of(payment, userId))
                .toList();
    }

}
