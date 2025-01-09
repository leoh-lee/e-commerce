package kr.hhplus.be.server.domain.payment.dto;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;

public record PaymentResult(
        Long id,
        Long orderId,
        int paymentPrice,
        PaymentStatus paymentStatus
) {

    public static PaymentResult fromEntity(Payment payment) {
        return new PaymentResult(payment.getId(), payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }
}
