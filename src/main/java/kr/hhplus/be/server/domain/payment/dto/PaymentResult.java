package kr.hhplus.be.server.domain.payment.dto;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResult(
        Long id,
        Long orderId,
        BigDecimal paymentPrice,
        PaymentStatus paymentStatus
) {

    public static PaymentResult fromEntity(Payment payment) {
        return new PaymentResult(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }
}
