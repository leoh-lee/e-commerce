package kr.hhplus.be.server.infrastructures.core.outbox;

import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
    List<Outbox> findByTopicContainingAndStatus(String topic, OutboxStatus status);

    @Query("SELECT o FROM Outbox o WHERE o.topic = :topic AND o.status != 'SUCCESS'")
    List<Outbox> findByTopicContainingAndStatusNotSuccess(@Param("topic") String topic);

    @Transactional
    @Modifying
    @Query("UPDATE Outbox o SET o.status = :status WHERE o.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") OutboxStatus outboxStatus);
}
