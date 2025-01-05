package kr.hhplus.be.server.interfaces.api.payment.response;

public record PaymentSearchResponse(
        Long id,
        Long orderId,
        Long userId,
        int paymentPrice,
        String orderStatus,
        String orderDate,
        String paymentDate,
        String paymentStatus
) {
}
