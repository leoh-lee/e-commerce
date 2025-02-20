package kr.hhplus.be.server.infrastructures.external.kafka.order.producer;

import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderEvent(String topic, OrderCreatedEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
