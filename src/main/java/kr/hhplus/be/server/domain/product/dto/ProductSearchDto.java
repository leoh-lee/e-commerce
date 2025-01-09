package kr.hhplus.be.server.domain.product.dto;

public record ProductSearchDto(
    String productName,
    Integer minPrice,
    Integer maxPrice
) {
}
