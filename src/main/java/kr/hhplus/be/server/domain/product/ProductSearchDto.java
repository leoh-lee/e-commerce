package kr.hhplus.be.server.domain.product;

public record ProductSearchDto(
    String productName,
    Integer minPrice,
    Integer maxPrice
) {
}
