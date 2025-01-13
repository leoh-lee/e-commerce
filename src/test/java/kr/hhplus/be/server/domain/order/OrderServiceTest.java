package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("사용자 ID로 주문 목록을 조회한다.")
    void getOrdersByUserId() {
        // given
        long userId = 1L;
        OrderPrice orderPrice = new OrderPrice();
        List<Order> orders = List.of(
                new Order(userId, 1L, orderPrice),
                new Order(userId, 1L, orderPrice),
                new Order(userId, 1L, orderPrice)
        );

        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> pageOrders = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findByUserId(userId, pageable)).thenReturn(pageOrders);

        // when
        Page<OrderSearchResult> results = orderService.getOrdersByUserId(userId, pageable);

        // then
        assertThat(results).hasSize(3);
        assertThat(results.getTotalPages()).isEqualTo(2);
        assertThat(results).extracting("userId")
                .containsExactly(userId, userId, userId);
    }

    @Test
    @DisplayName("상품을 주문한다.")
    void order_success() {
        // given
        List<OrderProductDto> orderProductDtos = List.of(
            new OrderProductDto(1L, 1),
            new OrderProductDto(2L, 5)
        );

        long userId = 1L;
        long couponId = 1L;
        long userCouponId = 1L;
        BigDecimal basePrice = BigDecimal.valueOf(20_000);
        BigDecimal discountPrice = BigDecimal.valueOf(10_000);
        BigDecimal finalPrice = BigDecimal.valueOf(10_000);

        OrderDto orderDto = new OrderDto(userId, orderProductDtos, couponId, userCouponId, basePrice, discountPrice, finalPrice);
        doNothing().when(orderRepository).save(any());
        doNothing().when(orderProductRepository).saveAll(any());

        // when
        OrderResult orderResult = orderService.order(orderDto);

        // then
        assertThat(orderResult)
                .extracting("userId", "couponId", "userCouponId", "basePrice", "discountAmount", "finalPrice")
                .containsExactly(userId, couponId, userCouponId, basePrice, discountPrice, finalPrice);
    }

    @Test
    @DisplayName("상위 주문 상품을 조회한다.")
    void getTopOrders_success() {
        // given
        TopOrderProductDto rank1 = new TopOrderProductDto(1L, 5);
        rank1.setRank(1);
        TopOrderProductDto rank2 = new TopOrderProductDto(2L, 4);
        rank2.setRank(2);
        TopOrderProductDto rank3 = new TopOrderProductDto(3L, 3);
        rank3.setRank(3);

        when(orderProductRepository.findTopOrderProducts(3)).thenReturn(List.of(rank1, rank2, rank3));

        // when
        List<OrderTopSearchResult> result = orderService.getTopOrders(3);

        // then
        assertThat(result).extracting("productId", "orderCount", "rank")
                .containsExactly(
                        tuple(rank1.getProductId(), rank1.getOrderCount(), rank1.getRank()),
                        tuple(rank2.getProductId(), rank2.getOrderCount(), rank2.getRank()),
                        tuple(rank3.getProductId(), rank3.getOrderCount(), rank3.getRank())
                );
    }

}