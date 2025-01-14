package kr.hhplus.be.server.domain.order.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
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
