package kr.hhplus.be.server.interfaces.api.order.request;

import kr.hhplus.be.server.domain.order.dto.OrderProductDto;

public record OrderProductsRequest(
        Long productId,
        int quantity
) {

    public OrderProductDto toDto() {
        return new OrderProductDto(productId, quantity);
    }
}
