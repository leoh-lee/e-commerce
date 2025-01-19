package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderPrice;
import kr.hhplus.be.server.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSearchResult(
        Long id,
        Long userId,
        Long userCouponId,
        BigDecimal basePrice,
        BigDecimal discountAmount,
        BigDecimal finalPrice,
        OrderStatus orderStatus,
        LocalDateTime orderDate
) {

    public static OrderSearchResult fromEntity(Order order) {
        OrderPrice orderPrice = order.getOrderPrice();

        return new OrderSearchResult(
                order.getId(),
                order.getUserId(),
                order.getUserCouponId(),
                orderPrice.getBasePrice(),
                orderPrice.getDiscountAmount(),
                orderPrice.getFinalPrice(),
                order.getOrderStatus(),
                order.getCreatedAt()
        );
    }

}
