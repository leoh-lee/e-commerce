package kr.hhplus.be.server.infrastructures.core.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {

    @Query(value = "select ph from PointHistory ph join fetch ph.point p where ph.point.userId = :userId")
    Page<PointHistory> findByUserId(long userId, Pageable pageable);

}
