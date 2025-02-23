package kr.hhplus.be.server.infrastructures.external.kafka.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public Outbox getOutboxByAggregateId(Long id) {
        return outboxRepository.findByAggregateId(String.valueOf(id));
    }

    @Transactional
    public void save(Outbox outbox) {
        outboxRepository.save(outbox);
    }

    @Transactional
    public int updateStatusById(Long id, OutboxStatus outboxStatus) {
        return outboxRepository.updateStatusById(id, outboxStatus);
    }

    public List<Outbox> getByTopicContainingAndStatus(String topic, OutboxStatus outboxStatus) {
        return outboxRepository.findByTopicContainingAndStatus(topic, outboxStatus);
    }

    public List<Outbox> getByTopicContainingAndStatusNotSuccess(String topic) {
        return outboxRepository.findByTopicContainingAndStatusNotSuccess(topic);
    }
}
