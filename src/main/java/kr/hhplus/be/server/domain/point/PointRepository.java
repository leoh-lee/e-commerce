package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {

    Optional<Point> findByUserId(Long userId);

    Optional<Point> findByUserIdWithLock(Long userId);

}
