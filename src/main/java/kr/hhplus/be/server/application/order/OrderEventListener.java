package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private static final String ORDER_CREATE_TOPIC = "order_create";
    private final OrderEventProducer orderEventProducer;
    private final OutboxRepository outboxRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(OrderSuccessEvent event) {
        Outbox outbox = Outbox.from(event, ORDER_CREATE_TOPIC);
        outboxRepository.save(outbox);
        event.setOutboxId(outbox.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void orderSuccessHandler(OrderSuccessEvent event) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(event.getUserId(), event.getOrderId(), event.getCouponId(), event.getEventTime());

        try {
            orderEventProducer.publishOrderEvent(ORDER_CREATE_TOPIC, orderCreatedEvent);
            outboxRepository.updateStatusById(event.getOrderId(), OutboxStatus.SUCCESS);
        } catch (Exception e) {
            outboxRepository.updateStatusById(event.getOrderId(), OutboxStatus.FAILED);
        }
    }

}
