package kr.hhplus.be.server.application.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OrderSuccessEvent {
    private final Long orderId;
    private final Long userId;
    private final Long couponId;
    private final BigDecimal price;
    private final LocalDateTime eventTime;
}
