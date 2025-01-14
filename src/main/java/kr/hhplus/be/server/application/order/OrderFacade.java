package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponUseResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.order.request.OrderProductsRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderTopSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
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
        
        // 1. 사용자 존재 여부만 확인
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException();
        }

        // 2. 상품 조회
        List<OrderProductsRequest> products = orderRequest.products();

        List<OrderProductDto> orderProductsDtos = products.stream()
                .map(OrderProductsRequest::toDto)
                .toList();

        BigDecimal totalPrice = productService.getTotalPriceBy(orderProductsDtos);

        // 3. 상품 재고 차감
        productService.decreaseProductStock(orderProductsDtos);
        
        // 4. 쿠폰 있으면 쿠폰 정보 조회
        OrderDto.OrderDtoBuilder orderDtoBuilder = OrderDto.builder()
                .userId(userId)
                .orderProductDtos(orderProductsDtos)
                .basePrice(totalPrice)
                .discountPrice(BigDecimal.ZERO)
                .finalPrice(totalPrice);

        Long userCouponId = orderRequest.userCouponId();

        if (!ObjectUtils.isEmpty(userCouponId)) {
            // 4-1. 쿠폰 상태 업데이트
            CouponUseResult couponUseResult = couponService.useCoupon(userCouponId, totalPrice);
            orderDtoBuilder
                    .userCouponId(userCouponId)
                    .couponId(couponUseResult.couponId())
                    .basePrice(couponUseResult.originalPrice())
                    .discountPrice(couponUseResult.discountedPrice())
                    .finalPrice(couponUseResult.finalPrice());
        }

        OrderDto orderDto = orderDtoBuilder.build();
        
        // 5. 주문 이력 저장
        OrderResult orderResult = orderService.order(orderDto);

        // 6. 데이터 플랫폼 전송
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
