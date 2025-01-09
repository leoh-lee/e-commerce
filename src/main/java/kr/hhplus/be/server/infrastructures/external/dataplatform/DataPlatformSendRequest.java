package kr.hhplus.be.server.infrastructures.external.dataplatform;

import java.time.LocalDateTime;

public record DataPlatformSendRequest<T> (
        String requestId,
        Long userId,
        RequestType requestType,
        LocalDateTime requestTime,

        T details
) {
}
