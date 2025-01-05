package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.interfaces.api.order.response.OrderProductsResponse;

import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        List<OrderProductsResponse> products,
        Long couponId,
        int basePrice,
        int discountAmount,
        int finalPrice,
        String orderDate,
        String orderStatus
) {
}
