package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;

public record ProductSearchRequest(
        String productName,
        Integer minPrice,
        Integer maxPrice
) {

    public ProductSearchDto toSearchDto() {
        return new ProductSearchDto(productName, minPrice, maxPrice);
    }
}
