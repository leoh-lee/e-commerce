package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointTransactionType;

import java.math.BigDecimal;

public record PointHistorySearchResult(
        Long id,
        Long pointId,
        PointTransactionType transactionType,
        BigDecimal amount
) {
    public static PointHistorySearchResult fromEntity(PointHistory pointHistory) {
        return new PointHistorySearchResult(pointHistory.getId(), pointHistory.getPoint().getId(), pointHistory.getTransactionType(), pointHistory.getAmount());
    }
}
