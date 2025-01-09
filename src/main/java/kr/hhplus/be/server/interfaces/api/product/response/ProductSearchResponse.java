package kr.hhplus.be.server.interfaces.api.product.response;

import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;

public record ProductSearchResponse(
        Long id,
        String name,
        int price,
        int stock
) {

    public static ProductSearchResponse from(ProductSearchResult searchResult) {
        ProductStock stock = searchResult.stock();

        return new ProductSearchResponse(searchResult.id(), searchResult.productName(), searchResult.productPrice(), stock.getStock());
    }
}
