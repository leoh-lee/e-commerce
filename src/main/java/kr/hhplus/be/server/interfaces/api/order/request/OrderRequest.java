package kr.hhplus.be.server.interfaces.api.order.request;

import java.util.List;

public record OrderRequest(
        Long userId,
        List<OrderProductsRequest> products,
        Long userCouponId
) {
}
