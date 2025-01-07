package kr.hhplus.be.server.domain.point;

public record PointHistorySearchResult(
        Long id,
        Long pointId,
        PointTransactionType transactionType,
        int amount
) {
    public static PointHistorySearchResult fromEntity(PointHistory pointHistory) {
        return new PointHistorySearchResult(pointHistory.getId(), pointHistory.getPoint().getId(), pointHistory.getTransactionType(), pointHistory.getAmount());
    }
}
