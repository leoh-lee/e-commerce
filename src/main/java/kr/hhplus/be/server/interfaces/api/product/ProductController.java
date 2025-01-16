package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.dto.ProductSearchRequest;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductFacade productFacade;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<ProductSearchResponse>> searchProducts(
            ProductSearchRequest searchRequest,
            Pageable pageable
    ) {
        return ApiResponse.ok(new PageResponse<>(productFacade.getProducts(searchRequest, pageable)), ResponseCode.SUCCESS_SEARCH_PRODUCTS);
    }

}
