package kr.hhplus.be.server.interfaces.api.product.response;

public record ProductTop5OrderResponse(
        Long id,
        int rank,
        String name,
        int price,
        int stock
) {
}
