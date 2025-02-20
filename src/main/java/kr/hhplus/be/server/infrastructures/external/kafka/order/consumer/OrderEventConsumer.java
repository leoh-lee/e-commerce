package kr.hhplus.be.server.infrastructures.external.kafka.order.consumer;

import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.infrastructures.external.kafka.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final DataPlatform dataPlatform;

    @KafkaListener(topics = "order_create", groupId = "order_create_group")
    public void listenOrderEvents(OrderCreatedEvent event) {
        DataPlatformSendRequest<Object> sendRequest = new DataPlatformSendRequest<>(event.userId(), RequestType.ORDER, event.timestamp(), event);
        dataPlatform.send(sendRequest);
    }

}
