package kr.hhplus.be.server.infrastructures.external.kafka.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.application.order.OrderSuccessEvent;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId;

    private String topic;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Lob
    private String payload;

    @CreatedDate
    private LocalDateTime createdAt;

    private int retry;

    public static Outbox from(OrderSuccessEvent orderSuccessEvent, String topic) {
        Outbox outbox = new Outbox();
        outbox.aggregateId = String.valueOf(orderSuccessEvent.getOrderId());
        outbox.topic = topic;
        outbox.status = OutboxStatus.PENDING;
        outbox.retry = 0;
        outbox.payload = orderSuccessEvent.toString();
        outbox.createdAt = orderSuccessEvent.getEventTime();

        return outbox;
    }

}
