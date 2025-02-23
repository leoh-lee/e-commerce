package kr.hhplus.be.server.infrastructures.external.kafka.order.consumer;

import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.infrastructures.external.kafka.config.KafkaTopicsProperties;
import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final DataPlatform dataPlatform;
    private final KafkaTopicsProperties topicsProperties;
    private final OutboxService outboxService;

    @KafkaListener(topics = "#{@kafkaTopicsProperties.order.created}", groupId = "order_create_group")
    public void listenOrderEvents(OrderCreatedEvent event) {
        log.info("Kafka Consumed. topic is '{}'", topicsProperties.getOrder().getCreated());
        DataPlatformSendRequest<Object> sendRequest = new DataPlatformSendRequest<>(event.userId(), RequestType.ORDER, event.timestamp(), event);
        dataPlatform.send(sendRequest);
        outboxService.getOutboxByAggregateId(event.orderId());
    }

}
