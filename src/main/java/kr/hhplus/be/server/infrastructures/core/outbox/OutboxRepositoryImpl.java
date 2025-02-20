package kr.hhplus.be.server.infrastructures.core.outbox;

import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public void save(Outbox outbox) {
        outboxJpaRepository.save(outbox);
    }

    @Override
    public int updateStatusById(Long id, OutboxStatus outboxStatus) {
        return outboxJpaRepository.updateStatusById(id, outboxStatus);
    }

    @Override
    public List<Outbox> findByTopicContainingAndStatus(String topic, OutboxStatus status) {
        return outboxJpaRepository.findByTopicContainingAndStatus(topic, status);
    }

    @Override
    public List<Outbox> findByTopicContainingAndStatusNotSuccess(String topic) {
        return outboxJpaRepository.findByTopicContainingAndStatusNotSuccess(topic);
    }
}
