package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.Point;

import java.math.BigDecimal;

public record PointUseResult(
        Long id,
        Long userId,
        BigDecimal balance
) {

    public static PointUseResult fromEntity(Point point) {
        return new PointUseResult(point.getId(), point.getUserId(), point.getBalance());
    }

}
