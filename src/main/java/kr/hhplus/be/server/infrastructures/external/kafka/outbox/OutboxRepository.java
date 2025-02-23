package kr.hhplus.be.server.infrastructures.external.kafka.outbox;

import java.util.List;

public interface OutboxRepository {

    void save(Outbox outbox);

    int updateStatusById(Long id, OutboxStatus outboxStatus);

    List<Outbox> findByTopicContainingAndStatus(String topic, OutboxStatus outboxStatus);

    List<Outbox> findByTopicContainingAndStatusNotSuccess(String topic);

    Outbox findByAggregateId(String aggregateId);
}
