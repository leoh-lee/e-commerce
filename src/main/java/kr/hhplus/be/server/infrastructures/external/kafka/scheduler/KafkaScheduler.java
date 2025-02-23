package kr.hhplus.be.server.infrastructures.external.kafka.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.infrastructures.external.kafka.config.KafkaTopicsProperties;
import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaScheduler {

    private final KafkaTopicsProperties topicsProperties;
    private final OrderEventProducer orderEventProducer;
    private final OutboxService outboxService;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void sendOutboxMessage() {
        String orderCreatedTopic = topicsProperties.getOrder().getCreated();
        List<Outbox> notSuccessEvents = outboxService.getByTopicContainingAndStatusNotSuccess(orderCreatedTopic);

        for (Outbox outbox : notSuccessEvents) {
            if (outbox.isRetryLimit()) {
                continue;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String payload = outbox.getPayload();
            try {
                OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(payload, OrderCreatedEvent.class);

                orderEventProducer.publishOrderEvent(orderCreatedTopic, orderCreatedEvent);

                outbox.published();
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            } catch (Exception e) {
                outbox.failed();
                log.error("Error KafkaScheduler sendOutboxMessage. Outbox ID >>> {}", outbox.getId(), e);
            } finally {
                outbox.increaseRetry();
            }
        }
    }
}
