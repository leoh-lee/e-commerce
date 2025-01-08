package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductSearchResult> searchProducts(ProductSearchDto searchDto, Pageable pageable) {
        return productRepository.findAll(searchDto, pageable).map(ProductSearchResult::fromEntity);
    }

    public ProductSearchResult searchProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

        return ProductSearchResult.fromEntity(product);
    }

}
