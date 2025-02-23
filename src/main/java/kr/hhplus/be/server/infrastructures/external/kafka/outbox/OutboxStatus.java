package kr.hhplus.be.server.infrastructures.external.kafka.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxStatus {
    PENDING,       // 아직 발행되지 않은 상태
    PUBLISHING,    // 발행 중
    SUCCESS,       // 발행 성공
    FAILED         // 발행 실패
}
