package kr.hhplus.be.server.interfaces.api.product.response;

public record ProductSearchResponse(
        Long id,
        String name,
        int price,
        int stock
) {
}
