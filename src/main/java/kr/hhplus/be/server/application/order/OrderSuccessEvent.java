package kr.hhplus.be.server.application.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public OrderSuccessEvent(Long orderId, Long userId, Long couponId, BigDecimal price, LocalDateTime eventTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.couponId = couponId;
        this.price = price;
        this.eventTime = eventTime;
    }

    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
