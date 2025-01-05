package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import kr.hhplus.be.server.interfaces.api.product.response.ProductTop5OrderResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "product", description = "상품 API")
public interface ProductApi {

    @Operation(summary = "상품 목록을 조회한다", description = "상품 목록을 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 목록 조회에 성공했습니다."),
    })
    @GetMapping
    ApiResponse<PageResponse<ProductSearchResponse>> searchProducts();

    @Operation(summary = "상위 주문 상품 목록을 조회한다", description = "상위 주문 상품 목록을 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상위 주문 상품 조회에 성공했습니다."),
    })
    @GetMapping("/top")
    ApiResponse<List<ProductTop5OrderResponse>> searchProductsTop5();

}
