package kr.hhplus.be.server.domain.payment.dto;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentStatus;

import java.math.BigDecimal;

public record PaymentSearchResult(
        Long id,
        Long userId,
        Long orderId,
        BigDecimal paymentPrice,
        PaymentStatus paymentStatus
) {

    public static PaymentSearchResult of(Payment payment, Long userId) {
        return new PaymentSearchResult(payment.getId(), userId, payment.getOrderId(), payment.getPaymentPrice(), payment.getPaymentStatus());
    }
}
