package kr.hhplus.be.server.interfaces.api.payment.request;

public record PaymentSearchRequest(
        Long userId,
        String startDate,
        String endDate
) {
}
