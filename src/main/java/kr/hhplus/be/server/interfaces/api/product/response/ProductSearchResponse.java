package kr.hhplus.be.server.interfaces.api.product.response;

import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;

import java.math.BigDecimal;

public record ProductSearchResponse(
        Long id,
        String name,
        BigDecimal price,
        int stock
) {

    public static ProductSearchResponse from(ProductSearchResult searchResult) {
        return new ProductSearchResponse(searchResult.id(), searchResult.productName(), searchResult.productPrice(), searchResult.stock());
    }
}
