package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.Product;

import java.math.BigDecimal;

public record ProductSearchResult(
        Long id,
        String productName,
        BigDecimal productPrice,
        int stock
) {

    public static ProductSearchResult fromEntity(Product product) {
        return new ProductSearchResult(product.getId(), product.getProductName(), product.getProductPrice(), product.getStock().getStock());
    }

}
