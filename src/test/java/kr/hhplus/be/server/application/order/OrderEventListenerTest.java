package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private OutboxRepository outboxRepository;

    @Test
    @DisplayName("outbox 저장 이벤트 발생 시 Outbox가 저장된다.")
    void saveOutbox_thenSaveOutbox() {
        // given
        OrderSuccessEvent orderSuccessEvent = new OrderSuccessEvent(1L, 1L, 1L, BigDecimal.ONE, LocalDateTime.now());

        // when
        orderEventListener.saveOutbox(orderSuccessEvent);

        // then
        verify(outboxRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("주문 성공 이벤트 발생 시, Produce에 실패하면(예외발생) Outbox의 상태를 FAIL로 업데이트 한다.")
    void orderSuccessHandler_whenProduceFail_thenUpdateOutboxStatusFAIL() {
        // given
        Long orderId = 1L;
        doThrow(RuntimeException.class).when(orderEventProducer).publishOrderEvent(any(), any());
        OrderSuccessEvent orderSuccessEvent = new OrderSuccessEvent(orderId, 1L, 1L, BigDecimal.ONE, LocalDateTime.now());

        // when
        orderEventListener.orderSuccessHandler(orderSuccessEvent);

        // then
        verify(outboxRepository, times(1)).updateStatusById(orderId, OutboxStatus.FAILED);
    }

    @Test
    @DisplayName("주문 성공 이벤트 발생 시, Produce에 성공하면 Outbox의 상태를 SUCCESS로 업데이트 한다.")
    void orderSuccessHandler_whenProduceSuccess_thenUpdateOutboxStatusSuccess() {
        // given
        Long orderId = 1L;
        OrderSuccessEvent orderSuccessEvent = new OrderSuccessEvent(orderId, 1L, 1L, BigDecimal.ONE, LocalDateTime.now());

        // when
        orderEventListener.orderSuccessHandler(orderSuccessEvent);

        // then
        verify(outboxRepository, times(1)).updateStatusById(orderId, OutboxStatus.SUCCESS);
    }

}