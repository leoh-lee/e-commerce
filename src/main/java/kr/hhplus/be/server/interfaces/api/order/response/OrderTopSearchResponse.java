package kr.hhplus.be.server.interfaces.api.order.response;

import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;

public record OrderTopSearchResponse(
        Long productId,
        int orderCount,
        int rank
) {

    public static OrderTopSearchResponse from(OrderTopSearchResult orderTopSearchResult) {
        return new OrderTopSearchResponse(
                orderTopSearchResult.productId(),
                orderTopSearchResult.orderCount(),
                orderTopSearchResult.rank()
        );
    }
}
