package kr.hhplus.be.server.interfaces.api.order.response;

import kr.hhplus.be.server.domain.order.dto.OrderResult;

public record OrderResponse(
        Long id,
        Long userId,
        Long couponId,
        int basePrice,
        int discountAmount,
        int finalPrice
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
