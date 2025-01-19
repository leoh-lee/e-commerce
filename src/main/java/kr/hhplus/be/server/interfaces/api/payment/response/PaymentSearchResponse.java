package kr.hhplus.be.server.interfaces.api.payment.response;

import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.dto.PaymentSearchResult;

import java.math.BigDecimal;

public record PaymentSearchResponse(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal paymentPrice,
        PaymentStatus paymentStatus
) {

    public static PaymentSearchResponse from(PaymentSearchResult searchResult) {
        return new PaymentSearchResponse(
                searchResult.id(),
                searchResult.orderId(),
                searchResult.userId(),
                searchResult.paymentPrice(),
                searchResult.paymentStatus()
        );
    }
}
