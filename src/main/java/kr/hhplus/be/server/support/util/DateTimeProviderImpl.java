package kr.hhplus.be.server.support.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeProviderImpl implements DateTimeProvider {
    @Override
    public LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

    @Override
    public Long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
