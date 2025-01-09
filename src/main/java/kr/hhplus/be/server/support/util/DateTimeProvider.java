package kr.hhplus.be.server.support.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public interface DateTimeProvider {

    default LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

}
