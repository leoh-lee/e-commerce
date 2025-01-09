package kr.hhplus.be.server.interfaces.api.payment.response;

import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.dto.PaymentResult;

public record PaymentResponse(
        Long id,
        Long orderId,
        int paymentPrice,
        PaymentStatus paymentStatus
) {

    public static PaymentResponse from(PaymentResult paymentResult) {
        return new PaymentResponse(
                paymentResult.id(),
                paymentResult.orderId(),
                paymentResult.paymentPrice(),
                paymentResult.paymentStatus()
        );
    }
}
