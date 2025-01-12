package kr.hhplus.be.server.domain.product.dto;

public record OrderProductsDto(
        Long productId,
        int quantity
) {
}
