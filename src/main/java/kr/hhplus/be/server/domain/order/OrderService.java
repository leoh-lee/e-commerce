package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.dto.OrderSearchResult;
import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;
import kr.hhplus.be.server.domain.order.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final EntityManager em;
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
                orderPrice,
                OrderStatus.ORDERED
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

    @Transactional
    public void updateOrderStatusPayed(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);

        order.updatePayed();
    }

    public List<OrderTopSearchResult> getTopOrders(int topCount) {
        return orderProductRepository.findTopOrderProducts(topCount)
                .stream()
                .map(OrderTopSearchResult::from)
                .toList();
    }

    public OrderSearchResult getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException());

        return OrderSearchResult.fromEntity(order);
    }

    @Transactional
    public OrderSearchResult getOrderByIdWithLockAndUpdateStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(OrderNotFoundException::new);
        
        order.updatePayed();

        return OrderSearchResult.fromEntity(order);
    }
}
