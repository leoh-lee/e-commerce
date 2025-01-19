package kr.hhplus.be.server.interfaces.api.order.response;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.dto.OrderSearchResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSearchResponse(
        Long id,
        Long userId,
        BigDecimal basePrice,
        BigDecimal discountAmount,
        BigDecimal finalPrice,
        OrderStatus orderStatus,
        LocalDateTime orderDate
) {

    public static OrderSearchResponse from(OrderSearchResult searchResult) {
        return new OrderSearchResponse(
                searchResult.id(),
                searchResult.userId(),
                searchResult.basePrice(),
                searchResult.discountAmount(),
                searchResult.finalPrice(),
                searchResult.orderStatus(),
                searchResult.orderDate()
        );
    }
}
