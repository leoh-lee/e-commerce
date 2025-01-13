package kr.hhplus.be.server.domain.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long userId,
        List<OrderProductDto> orderProductDtos,
        Long couponId,
        Long userCouponId,
        BigDecimal basePrice,
        BigDecimal discountPrice,
        BigDecimal finalPrice
) {
}
