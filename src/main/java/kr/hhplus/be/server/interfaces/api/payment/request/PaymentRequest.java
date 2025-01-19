package kr.hhplus.be.server.interfaces.api.payment.request;

public record PaymentRequest(
        Long userId,
        Long orderId
) {
}
