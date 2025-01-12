package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;

import java.math.BigDecimal;

public record ProductSearchRequest(
        String productName,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {

    public ProductSearchDto toSearchDto() {
        return new ProductSearchDto(productName, minPrice, maxPrice);
    }
}
