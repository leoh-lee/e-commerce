package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderTopSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ApiResponse<OrderResponse> order(@RequestBody OrderRequest orderRequest) {
        return ApiResponse.ok(orderFacade.order(orderRequest), ResponseCode.SUCCESS_ORDER);
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<OrderSearchResponse>> searchOrders(@RequestParam("userId") Long userId, Pageable pageable) {
        return ApiResponse.ok(new PageResponse<>(orderFacade.getOrdersByUserId(userId, pageable)), ResponseCode.SUCCESS_SEARCH_ORDERS);
    }

    @Override
    @GetMapping("/top")
    public ApiResponse<List<OrderTopSearchResponse>> searchProductsTop5() {
        return ApiResponse.ok(orderFacade.searchTopOrder(), ResponseCode.SUCCESS_SEARCH_TOP_ORDERS);
    }
}
