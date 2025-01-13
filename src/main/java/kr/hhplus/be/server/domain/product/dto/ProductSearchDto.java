package kr.hhplus.be.server.domain.product.dto;

import java.math.BigDecimal;

public record ProductSearchDto(
    String productName,
    BigDecimal minPrice,
    BigDecimal maxPrice
) {
}
