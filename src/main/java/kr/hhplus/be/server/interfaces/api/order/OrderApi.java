package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.interfaces.api.order.response.OrderTopSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;

import java.util.List;

@Tag(name = "order", description = "주문 API")
public interface OrderApi {

    @Operation(summary = "상품을 주문한다", description = "상품을 주문한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품을 성공적으로 주문했습니다.", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5500\", \"message\": \"사용자를 찾을 수 없습니다.\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5100\", \"message\": \"상품을 찾을 수 없습니다.\" }")))
    })
    ApiResponse<OrderResponse> order(@RequestBody OrderRequest orderRequest);

    @Operation(summary = "주문 목록을 조회한다", description = "주문 목록을 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록 조회에 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5500\", \"message\": \"사용자를 찾을 수 없습니다.\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "주문 목록 조회 실패", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5300\", \"message\": \"주문 목록 조회 실패\" }")))
    })
    ApiResponse<PageResponse<OrderSearchResponse>> searchOrders(@RequestParam Long userId, Pageable pageable);

    @Operation(summary = "상위 주문 상품 목록을 조회한다", description = "상위 주문 상품 목록을 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상위 주문 상품 조회에 성공했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상위 상품 조회 실패", content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"code\": \"5101\", \"message\": \"상위 상품 조회 실패\" }")))
    })
    ApiResponse<List<OrderTopSearchResponse>> searchProductsTop5();
}
