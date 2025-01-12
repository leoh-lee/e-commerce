package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.Point;

import java.math.BigDecimal;

public record PointChargeResult(
        Long id,
        Long userId,
        BigDecimal balance
) {

    public static PointChargeResult fromEntity(Point point) {
        return new PointChargeResult(point.getId(), point.getUserId(), point.getBalance());
    }

}
