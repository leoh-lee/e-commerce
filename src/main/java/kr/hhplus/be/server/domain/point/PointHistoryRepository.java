package kr.hhplus.be.server.domain.point;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryRepository {

    Page<PointHistory> findByUserId(long userId, Pageable pageable);

    void save(PointHistory pointHistory);

}
