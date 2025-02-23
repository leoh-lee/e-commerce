package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.infrastructures.external.kafka.config.KafkaTopicsProperties;
import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxService;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final OrderEventProducer orderEventProducer;
    private final OutboxService outboxService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(OrderSuccessEvent event) {
        Outbox outbox = Outbox.from(event, kafkaTopicsProperties.getOrder().getCreated());
        outboxService.save(outbox);
        event.setOutboxId(outbox.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)  // phase의 Default가 AFTER_COMMIT이나, 명시하는 것이 가독성이 좋을 듯.
    public void orderSuccessHandler(OrderSuccessEvent event) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(event.getUserId(), event.getOrderId(), event.getCouponId(), event.getEventTime());

        try {
            orderEventProducer.publishOrderEvent(kafkaTopicsProperties.getOrder().getCreated(), orderCreatedEvent);
            outboxService.updateStatusById(event.getOrderId(), OutboxStatus.SUCCESS);
        } catch (Exception e) {
            log.info("Kafka publish failed. Error is ", e);
            outboxService.updateStatusById(event.getOrderId(), OutboxStatus.FAILED);
        }
    }

}
