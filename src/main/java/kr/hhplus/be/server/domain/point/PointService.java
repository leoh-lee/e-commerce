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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointUseResult usePoint(Long userId, BigDecimal amount) {
        Point findPoint = findPointByUserId(userId, true);
        findPoint.usePoint(amount);

        return PointUseResult.fromEntity(findPoint);
    }

    public PointChargeResult chargePoint(Long userId, BigDecimal amount) {
        Point findPoint = findPointByUserId(userId, true);
        findPoint.chargePoint(amount);

        // chargePoint에 history 저장 책임이 있는 것이 무겁다..
        PointHistory pointHistory = new PointHistory(findPoint, PointTransactionType.CHARGE, amount);
        pointHistoryRepository.save(pointHistory);

        return PointChargeResult.fromEntity(findPoint);
    }

    @Transactional(readOnly = true)
    public PointSearchResult getPointByUserId(long userId) {
        return PointSearchResult.fromEntity(findPointByUserId(userId, false));
    }

    @Transactional(readOnly = true)
    public Page<PointHistorySearchResult> getPointHistoriesByUserId(long userId, Pageable pageable) {
        return pointHistoryRepository.findByUserId(userId, pageable).map(PointHistorySearchResult::fromEntity);
    }

    public void createDefaultPoint(Long userId) {
        Point point = new Point(userId, BigDecimal.ZERO);

        pointRepository.save(point);
    }

    private Point findPointByUserId(long userId, boolean isLock) {
        if (isLock) {
            return pointRepository.findByUserIdWithLock(userId).orElseThrow(PointNotFoundException::new);
        }

        return pointRepository.findByUserId(userId).orElseThrow(PointNotFoundException::new);
    }
}
