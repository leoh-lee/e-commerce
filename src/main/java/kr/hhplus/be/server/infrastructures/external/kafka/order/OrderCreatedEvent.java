package kr.hhplus.be.server.infrastructures.external.kafka.order;

import java.time.LocalDateTime;

public record OrderCreatedEvent (
        Long userId,
        Long orderId,
        Long couponId,
        LocalDateTime timestamp
) {
}
