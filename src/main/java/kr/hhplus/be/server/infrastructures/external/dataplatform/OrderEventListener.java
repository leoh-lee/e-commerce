package kr.hhplus.be.server.infrastructures.external.dataplatform;

import kr.hhplus.be.server.application.order.OrderSuccessEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderEventProducer orderEventProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void orderSuccessHandler(OrderSuccessEvent event) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(event.getUserId(), event.getOrderId(), event.getCouponId(), event.getEventTime());
        orderEventProducer.publishOrderEvent(orderCreatedEvent);
    }

}
