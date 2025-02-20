package kr.hhplus.be.server.infrastructures.external.dataplatform;

import kr.hhplus.be.server.application.order.OrderSuccessEvent;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void orderSuccessHandler(OrderSuccessEvent event) {
        DataPlatformSendRequest<OrderSuccessEvent> dataPlatformSendRequest = new DataPlatformSendRequest<>(event.getUserId(), RequestType.ORDER, dateTimeProvider.getLocalDateTimeNow(), event);
        dataPlatform.send(dataPlatformSendRequest);
    }

}
