package kr.hhplus.be.server.domain.order.dto;

public record OrderProductDto(
        Long productId,
        int quantity
) {
}
