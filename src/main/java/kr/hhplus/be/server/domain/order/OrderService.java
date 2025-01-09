package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    public Page<OrderSearchResult> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        return orders
                .map(OrderSearchResult::fromEntity);
    }

    @Transactional
    public OrderResult order(OrderDto orderDto) {
        OrderPrice orderPrice = new OrderPrice(orderDto.basePrice(), orderDto.discountPrice(), orderDto.finalPrice());

        Order order = new Order(
                orderDto.userId(),
                orderDto.userCouponId(),
                orderPrice
        );

        orderRepository.save(order);

        List<OrderProductDto> orderProductDtos = orderDto.orderProductDtos();

        List<OrderProduct> orderProducts = orderProductDtos.stream()
                .map(orderProductDto -> new OrderProduct(order.getId(), orderProductDto.productId(), orderProductDto.quantity()))
                .toList();

        orderProductRepository.saveAll(orderProducts);

        return new OrderResult(
                order.getId(),
                orderDto.userId(),
                orderDto.couponId(),
                orderDto.userCouponId(),
                orderPrice.getBasePrice(),
                orderPrice.getDiscountAmount(),
                orderPrice.getFinalPrice()
        );
    }

    public List<OrderTopSearchResult> getTopOrders(int topCount) {
        return orderProductRepository.findTopOrderProducts(topCount)
                .stream()
                .map(OrderTopSearchResult::from)
                .toList();
    }

}
