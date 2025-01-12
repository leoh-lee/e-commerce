package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructures.core.point.PointHistoryRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Testcontainers
@Import(PointHistoryRepositoryImpl.class)
class PointHistoryRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("유저 ID로 포인트 충전/사용 내역을 조회한다.")
    void findByUserId() {
        // given
        User user = new User("testUser");
        em.persist(user);

        Point point = new Point(user.getId(), 0);
        em.persist(point);

        for (int i = 0; i < 5; i++) {
            PointHistory pointHistory1 = new PointHistory(point, PointTransactionType.CHARGE, 10_000);
            PointHistory pointHistory2 = new PointHistory(point, PointTransactionType.USE, 2_000);

            em.persist(pointHistory1);
            em.persist(pointHistory2);
        }

        int pageSize = 5;

        Pageable pageable = PageRequest.of(0, pageSize);

        // when
        Page<PointHistory> pointHistories = pointHistoryRepository.findByUserId(user.getId(), pageable);

        // then
        assertThat(pointHistories.getContent()).hasSize(pageSize);
        assertThat(pointHistories.getTotalElements()).isEqualTo(10);
        assertThat(pointHistories.getTotalPages()).isEqualTo(2);
        assertThat(pointHistories.getContent().getFirst()).extracting("transactionType", "amount").containsExactly(PointTransactionType.CHARGE, 10_000);
    }
}