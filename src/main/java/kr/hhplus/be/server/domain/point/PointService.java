package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.dto.PointChargeResult;
import kr.hhplus.be.server.domain.point.dto.PointHistorySearchResult;
import kr.hhplus.be.server.domain.point.dto.PointSearchResult;
import kr.hhplus.be.server.domain.point.dto.PointUseResult;
import kr.hhplus.be.server.domain.point.exception.PointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointUseResult usePoint(Long userId, int amount) {
        Point findPoint = findPointByUserId(userId, true);
        findPoint.usePoint(amount);

        return PointUseResult.fromEntity(findPoint);
    }

    @Transactional
    public PointChargeResult chargePoint(Long userId, int amount) {
        Point findPoint = findPointByUserId(userId, true);
        findPoint.chargePoint(amount);

        PointHistory pointHistory = new PointHistory(findPoint, PointTransactionType.CHARGE, amount);
        pointHistoryRepository.save(pointHistory);

        return PointChargeResult.fromEntity(findPoint);
    }

    public PointSearchResult getPointByUserId(long userId) {
        return PointSearchResult.fromEntity(findPointByUserId(userId, false));
    }

    public Page<PointHistorySearchResult> getPointHistoriesByUserId(long userId, Pageable pageable) {
        return pointHistoryRepository.findByUserId(userId, pageable).map(PointHistorySearchResult::fromEntity);
    }

    public void createDefaultPoint(Long userId) {
        Point point = new Point(userId, 0);

        pointRepository.save(point);
    }

    private Point findPointByUserId(long userId, boolean isLock) {
        if (isLock) {
            return pointRepository.findByUserIdWithLock(userId).orElseThrow(PointNotFoundException::new);
        }

        return pointRepository.findByUserId(userId).orElseThrow(PointNotFoundException::new);
    }
}
