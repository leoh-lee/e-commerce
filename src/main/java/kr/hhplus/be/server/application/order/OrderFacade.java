package kr.hhplus.be.server.application.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponUseResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.infrastructures.external.redis.CacheScheduler;
import kr.hhplus.be.server.interfaces.api.order.request.OrderProductsRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderTopSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static kr.hhplus.be.server.infrastructures.external.redis.CacheScheduler.PRODUCTS_TOP_5_CACHE_KEY;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CouponService couponService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheScheduler cacheScheduler;
    private final ObjectMapper objectMapper;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
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
        orderProductsDtos.forEach(productService::decreaseProductStock);

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
        orderEventPublisher.success(new OrderSuccessEvent(orderResult.orderId(), orderResult.userId(), null));

        return OrderResponse.from(orderResult);
    }

    @Transactional(readOnly = true)
    public Page<OrderSearchResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderService.getOrdersByUserId(userId, pageable)
                .map(OrderSearchResponse::from);
    }

    @Transactional(readOnly = true)
    public List<OrderTopSearchResponse> searchTopOrder() {
        String topOrdersJson = (String) redisTemplate.opsForValue().get(PRODUCTS_TOP_5_CACHE_KEY);

        if (!StringUtils.hasText(topOrdersJson)) {
            List<OrderTopSearchResult> orderTopSearchResults = cacheScheduler.cacheTopOrderProducts();

            return orderTopSearchResults
                    .stream()
                    .map(OrderTopSearchResponse::from)
                    .toList();
        }

        List<OrderTopSearchResult> orderTopSearchResults = null;

        try {
            orderTopSearchResults = objectMapper.readValue(topOrdersJson, new TypeReference<List<OrderTopSearchResult>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return orderTopSearchResults
                .stream()
                .map(OrderTopSearchResponse::from)
                .toList();
    }

}
