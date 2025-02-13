package kr.hhplus.be.server.application.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(OrderSuccessEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
