package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderProductsResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderApi {

    @Override
    public ApiResponse<OrderResponse> order(@RequestBody OrderRequest orderRequest) {

        List<OrderProductsResponse> orderProducts = List.of(
                new OrderProductsResponse(1L, 1),
                new OrderProductsResponse(2L, 3)
        );

        OrderResponse result = new OrderResponse(
                1L,
                1L,
                1L,
                30_000,
                12_000,
                18_000
        );

        return ApiResponse.ok(result, ResponseCode.SUCCESS_ORDER);
    }

    @Override
    public ApiResponse<PageResponse<OrderSearchResponse>> searchOrders(@RequestParam Long userId) {

        List<OrderProductsResponse> orderProducts = List.of(
                new OrderProductsResponse(1L, 1),
                new OrderProductsResponse(2L, 3)
        );

        List<OrderSearchResponse> result = List.of(
                new OrderSearchResponse(
                        1L,
                        orderProducts,
                        1L,
                        30_000,
                        12_000,
                        18_000,
                        "2024-01-5",
                        "PENDING"
                ),
                new OrderSearchResponse(
                        2L,
                        orderProducts,
                        null,
                        10_000,
                        2_000,
                        8_000,
                        "2024-01-5",
                        "PENDING"
                )
        );

        Page<OrderSearchResponse> pageResult = new PageImpl<>(result);

        return ApiResponse.ok(new PageResponse<>(pageResult), ResponseCode.SUCCESS_SEARCH_ORDERS);
    }
}
