package kr.hhplus.be.server.domain.order.dto;

public record OrderResult(
        Long orderId,
        Long userId,
        Long couponId,
        Long userCouponId,
        int basePrice,
        int discountAmount,
        int finalPrice
) {
}
