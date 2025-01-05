package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "order", description = "주문 API")
public interface OrderApi {

    @Operation(summary = "상품을 주문한다", description = "상품을 주문한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품을 성공적으로 주문했습니다.", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품을 찾을 수 없습니다.", content = @Content)
    })
    @PostMapping
    ApiResponse<OrderResponse> order(@RequestBody OrderRequest orderRequest);

    @Operation(summary = "상위 주문 상품 목록을 조회한다", description = "상위 주문 상품 목록을 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상위 주문 상품 조회에 성공했습니다."),
    })
    @GetMapping
    ApiResponse<PageResponse<OrderSearchResponse>> searchOrders(@RequestParam Long userId);

}
