package kr.hhplus.be.server.application.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class OrderSuccessEvent {
    private Long orderId;
    private Long userId;
    private Long couponId;
    private BigDecimal price;
    private LocalDateTime eventTime;

    @Setter
    private Long outboxId;

    public OrderSuccessEvent(Long orderId, Long userId, Long couponId, BigDecimal price, LocalDateTime eventTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.couponId = couponId;
        this.price = price;
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "OrderSuccessEvent{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", couponId=" + couponId +
                ", price=" + price +
                ", eventTime=" + eventTime +
                '}';
    }
}
