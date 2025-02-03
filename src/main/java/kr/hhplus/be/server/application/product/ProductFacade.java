package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductSearchRequest;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    @Transactional(readOnly = true)
    public Page<ProductSearchResponse> getProducts(ProductSearchRequest searchRequest, Pageable pageable) {
        return productService.searchProducts(searchRequest.toSearchDto(), pageable)
                .map(ProductSearchResponse::from);
    }

}
