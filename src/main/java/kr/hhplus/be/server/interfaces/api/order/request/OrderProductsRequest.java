package kr.hhplus.be.server.interfaces.api.order.request;

public record OrderProductsRequest(
        Long productId,
        int quantity
) {
}
