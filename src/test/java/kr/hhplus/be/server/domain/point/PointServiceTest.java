package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.dto.PointChargeResult;
import kr.hhplus.be.server.domain.point.dto.PointHistorySearchResult;
import kr.hhplus.be.server.domain.point.dto.PointSearchResult;
import kr.hhplus.be.server.domain.point.dto.PointUseResult;
import kr.hhplus.be.server.domain.point.exception.PointNotFoundException;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("사용자 ID로 포인트를 조회한다.")
    void getPointByUserId_returnsPoint() {
        // given
        long userId = 1L;

        long pointId = 1L;
        BigDecimal balance = BigDecimal.valueOf(100_000);

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.of(new Point(pointId, userId, balance)));

        // when
        PointSearchResult result = pointService.getPointByUserId(userId);

        // then
        assertThat(result)
                .extracting("id", "userId", "balance")
                .containsExactly(pointId, userId, balance);
    }

    @Test
    @DisplayName("사용자 ID로 포인트 조회 시 조회 데이터가 없는 경우 PointNotFoundException 예외가 발생한다.")
    void getPointByUserId_throwsPointNotFoundException() {
        // given
        long userId = 1L;

        when(pointRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(()-> pointService.getPointByUserId(userId))
                .isInstanceOf(PointNotFoundException.class);
    }

    @Test
    @DisplayName("포인트를 사용(차감)한다.")
    void usePoint_success() {
        // given
        BigDecimal amount = BigDecimal.valueOf(90_000);
        BigDecimal balance = BigDecimal.valueOf(100_000);
        long userId = 1L;

        Point point = new Point(1L, userId, balance);

        when(pointRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(point));

        // when
        PointUseResult pointUseResult = pointService.usePoint(userId, amount);

        // then
        assertThat(pointUseResult.balance()).isEqualTo(balance.subtract(amount));
    }

    @Test
    @DisplayName("포인트를 충전한다.")
    void chargePoint_success() {
        // given
        BigDecimal amount = BigDecimal.valueOf(90_000);
        BigDecimal balance = BigDecimal.valueOf(100_000);
        long userId = 1L;

        Point point = new Point(1L, userId, balance);

        when(pointRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(point));

        // when
        PointChargeResult pointChargeResult = pointService.chargePoint(userId, amount);

        // then
        assertThat(pointChargeResult.balance()).isEqualTo(balance.add(amount));
    }

    @Test
    @DisplayName("포인트를 충전하면 포인트 충전 내역을 저장한다.")
    void savePointHistory_whenSavePoint() {
        // given
        BigDecimal amount = BigDecimal.valueOf(90_000);
        BigDecimal balance = BigDecimal.valueOf(100_000);
        long userId = 1L;

        Point point = new Point(1L, userId, balance);

        when(pointRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(point));

        // when
        pointService.chargePoint(userId, amount);

        // then
        verify(pointHistoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("포인트 충전/사용 이력을 조회한다.")
    void getPointHistory_success() {
        // given
        BigDecimal balance = BigDecimal.valueOf(100_000);
        long userId = 1L;
        long pointId = 1L;

        Point point = new Point(pointId, userId, balance);

        Pageable pageable = PageRequest.of(0, 2);

        int total = 2;

        Page<PointHistory> result = new PageImpl<>(
                List.of(
                        new PointHistory(1L, point, PointTransactionType.CHARGE, BigDecimal.valueOf(10_000)),
                        new PointHistory(2L, point, PointTransactionType.USE, BigDecimal.valueOf(5_000))
                ),
                pageable, total
        );

        when(pointHistoryRepository.findByUserId(userId, pageable)).thenReturn(result);

        // when
        Page<PointHistorySearchResult> searchResults = pointService.getPointHistoriesByUserId(userId, pageable);

        // then
        assertThat(searchResults.getSize()).isEqualTo(total);
        assertThat(searchResults.getContent()).extracting("id", "pointId", "transactionType", "amount")
                .containsExactly(
                        tuple(1L, pointId, PointTransactionType.CHARGE, BigDecimal.valueOf(10_000)),
                        tuple(2L, pointId, PointTransactionType.USE, BigDecimal.valueOf(5_000))
                );
    }

    @Test
    @DisplayName("사용자의 기본 포인트를 생성한다")
    void createDefaultPoint_success() {
        // given
        long userId = 1L;

        // when
        pointService.createDefaultPoint(userId);

        // then
        verify(pointRepository, times(1)).save(any());
    }

}
