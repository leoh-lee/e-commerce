package kr.hhplus.be.server.infrastructures.external.kafka.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaSchedulerTest {

    private static final String ORDER_CREATE_TOPIC = "order_create";
    @Mock
    private OrderEventProducer orderEventProducer;
    
    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private KafkaScheduler kafkaScheduler;

    @Test
    @DisplayName("outbox Message 발행에 성공하면, outbox의 retry가 1 증가하고 SUCCESS 상태로 변경된다.")
    void sendOutboxMessage_happyPath() throws Exception {
        // given
        Outbox outbox = createTestOutbox(0);
        int originalRetry = outbox.getRetry();

        List<Outbox> outboxes = List.of(outbox);

        when(outboxRepository.findByTopicContainingAndStatusNotSuccess(ORDER_CREATE_TOPIC)).thenReturn(outboxes);

        // when
        kafkaScheduler.sendOutboxMessage();

        // then
        // OrderCreatedEvent에 대한 Json 변환도 검증됨
        verify(orderEventProducer).publishOrderEvent(eq(ORDER_CREATE_TOPIC), any(OrderCreatedEvent.class));
        assertThat(outbox.getRetry()).isEqualTo(originalRetry + 1);
        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.SUCCESS);
    }

    @Test
    @DisplayName("outbox Message 발행 시 예외가 발생하면, outbox의 retry가 1 증가하고 FAIL 상태로 변경된다.")
    void sendOutboxMessage_whenPublishThrowsException_updatesStatusToFailed() throws Exception {
        // given
        Outbox outbox = createTestOutbox(0);
        int originalRetry = outbox.getRetry();

        List<Outbox> outboxes = List.of(outbox);

        when(outboxRepository.findByTopicContainingAndStatusNotSuccess(ORDER_CREATE_TOPIC)).thenReturn(outboxes);
        doThrow(RuntimeException.class).when(orderEventProducer).publishOrderEvent(eq(ORDER_CREATE_TOPIC), any(OrderCreatedEvent.class));

        // when
        kafkaScheduler.sendOutboxMessage();

        // then
        // OrderCreatedEvent에 대한 Json 변환도 검증됨
        assertThat(outbox.getRetry()).isEqualTo(originalRetry + 1);
        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.FAILED);
    }

    @Test
    @DisplayName("outbox Message 발행 시 JsonProcessingException 예외가 발생하면, IllegalArgumentException으로 예외가 변환된다.")
    void sendOutboxMessage_whenJsonProcessingException_throwsRuntimeException() {
        // given
        Outbox outbox = new Outbox(1L, "1", ORDER_CREATE_TOPIC, OutboxStatus.PENDING, "invalid payload", LocalDateTime.now(), 0);

        List<Outbox> outboxes = List.of(outbox);
        when(outboxRepository.findByTopicContainingAndStatusNotSuccess(ORDER_CREATE_TOPIC)).thenReturn(outboxes);

        // then
        assertThrows(IllegalArgumentException.class, () -> kafkaScheduler.sendOutboxMessage());
    }

    @Test
    @DisplayName("outbox Message 발행 시 retry 횟수 초과 시 발행하지 않는다.")
    void sendOutboxMessage_whenRetryLimit_skipProcessing() throws JsonProcessingException {
        // given
        Outbox outbox = createTestOutbox(3);

        int originalRetry = outbox.getRetry();
        OutboxStatus originalStatus = outbox.getStatus();

        List<Outbox> outboxes = List.of(outbox);
        when(outboxRepository.findByTopicContainingAndStatusNotSuccess(ORDER_CREATE_TOPIC)).thenReturn(outboxes);

        // when
        kafkaScheduler.sendOutboxMessage();

        // then
        verify(orderEventProducer, never()).publishOrderEvent(anyString(), any());
        assertThat(outbox.getRetry()).isEqualTo(originalRetry);
        assertThat(outbox.getStatus()).isEqualTo(originalStatus);
    }

    private Outbox createTestOutbox(int retry) throws JsonProcessingException {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 1L, 1L, LocalDateTime.now());
        String payload = new ObjectMapper().writeValueAsString(event);

        return new Outbox(1L, "1", ORDER_CREATE_TOPIC, OutboxStatus.PENDING, payload, LocalDateTime.now(), retry);
    }
}