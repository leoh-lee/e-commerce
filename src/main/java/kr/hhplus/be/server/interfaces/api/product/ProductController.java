package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import kr.hhplus.be.server.interfaces.api.product.response.ProductTop5OrderResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    @Override
    public ApiResponse<PageResponse<ProductSearchResponse>> searchProducts() {

        List<ProductSearchResponse> result = List.of(
                new ProductSearchResponse(1L, "상품1", 10_000, 100),
                new ProductSearchResponse(2L, "상품2", 20_000, 200)
        );

        Page<ProductSearchResponse> pageResult = new PageImpl<>(result);

        return ApiResponse.ok(new PageResponse<>(pageResult), ResponseCode.SUCCESS_SEARCH_PRODUCTS);
    }

    @Override
    public ApiResponse<List<ProductTop5OrderResponse>> searchProductsTop5() {

        List<ProductTop5OrderResponse> result = List.of(
                new ProductTop5OrderResponse(1L, 1, "상품1", 10_000, 100),
                new ProductTop5OrderResponse(2L, 2, "상품2", 20_000, 200),
                new ProductTop5OrderResponse(3L, 3, "상품3", 30_000, 150),
                new ProductTop5OrderResponse(4L, 4, "상품4", 40_000, 300),
                new ProductTop5OrderResponse(5L, 5, "상품5", 50_000, 40)
        );

        return ApiResponse.ok(result, ResponseCode.SUCCESS_SEARCH_TOP_ORDERS);
    }

}
