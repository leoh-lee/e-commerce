package kr.hhplus.be.server.interfaces.api.order.response;

import java.util.List;

public record OrderSearchResponse(
        Long id,
        List<OrderProductsResponse> products,
        Long couponId,
        int basePrice,
        int discountAmount,
        int finalPrice,
        String orderDate,
        String orderStatus
) {
}
