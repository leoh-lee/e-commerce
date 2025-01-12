package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponUseResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserSearchResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.order.request.OrderProductsRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderTopSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CouponService couponService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;

    public OrderResponse order(OrderRequest orderRequest) {
        Long userId = orderRequest.userId();
        UserSearchResult userById = userService.getUserById(userId);

        List<OrderProductsRequest> products = orderRequest.products();

        List<OrderProductDto> orderProductsDtos = products.stream()
                .map(OrderProductsRequest::toDto)
                .toList();

        int totalPrice = productService.getTotalPriceBy(orderProductsDtos);
        productService.decreaseProductStock(orderProductsDtos);

        Long userCouponId = orderRequest.userCouponId();

        CouponUseResult couponUseResult = couponService.useCoupon(userCouponId, totalPrice);
        OrderDto orderDto = new OrderDto(userId, orderProductsDtos, couponUseResult.couponId(), userCouponId, couponUseResult.originalPrice(), couponUseResult.discountedPrice(), couponUseResult.finalPrice());

        OrderResult orderResult = orderService.order(orderDto);
        dataPlatform.send(new DataPlatformSendRequest<>(userId, RequestType.ORDER, dateTimeProvider.getLocalDateTimeNow(), orderResult));

        return OrderResponse.from(orderResult);
    }

    public List<OrderTopSearchResponse> searchTopOrder(int topCount) {
        return orderService.getTopOrders(topCount)
                .stream()
                .map(OrderTopSearchResponse::from)
                .toList();

    }

}
