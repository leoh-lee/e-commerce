package kr.hhplus.be.server.domain.order.dto;

import java.util.List;

public record OrderDto(
        Long userId,
        List<OrderProductDto> orderProductDtos,
        Long couponId,
        Long userCouponId,
        int basePrice,
        int discountPrice,
        int finalPrice
) {
}
