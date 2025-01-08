package kr.hhplus.be.server.domain.product;

public record ProductSearchResult(
        Long id,
        String productName,
        int productPrice,
        ProductStock stock
) {

    public static ProductSearchResult fromEntity(Product product) {
        return new ProductSearchResult(product.getId(), product.getProductName(), product.getProductPrice(), product.getStock());
    }

}
