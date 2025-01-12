package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderPrice;

import java.math.BigDecimal;

public record OrderSearchResult(
        Long id,
        Long userId,
        Long userCouponId,
        BigDecimal basePrice,
        BigDecimal discountAmount,
        BigDecimal finalPrice
) {

    public static OrderSearchResult fromEntity(Order order) {
        OrderPrice orderPrice = order.getOrderPrice();

        return new OrderSearchResult(
                order.getId(),
                order.getUserId(),
                order.getUserCouponId(),
                orderPrice.getBasePrice(),
                orderPrice.getDiscountAmount(),
                orderPrice.getFinalPrice()
        );
    }

}
