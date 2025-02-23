package kr.hhplus.be.server.infrastructures.external.kafka.order.producer;

import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderEvent(String topic, OrderCreatedEvent event) {
        kafkaTemplate.send(topic, event).whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                log.error("[Kafka] Order Create 이벤트 발행 실패.", throwable);
            }
        });
    }
}
