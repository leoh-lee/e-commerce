package kr.hhplus.be.server.domain.order.dto;

import java.math.BigDecimal;

public record OrderResult(
        Long orderId,
        Long userId,
        Long couponId,
        Long userCouponId,
        BigDecimal basePrice,
        BigDecimal discountAmount,
        BigDecimal finalPrice
) {
}
