package kr.hhplus.be.server.interfaces.api.order.response;

import kr.hhplus.be.server.domain.order.dto.OrderResult;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        Long userId,
        Long couponId,
        BigDecimal basePrice,
        BigDecimal discountAmount,
        BigDecimal finalPrice
) {

    public static OrderResponse from(OrderResult orderResult) {
        return new OrderResponse(
                orderResult.orderId(),
                orderResult.userId(),
                orderResult.couponId(),
                orderResult.basePrice(),
                orderResult.discountAmount(),
                orderResult.finalPrice()
        );
    }
}
