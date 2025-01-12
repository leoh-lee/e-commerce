package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.Point;

import java.math.BigDecimal;

public record PointSearchResult(
        Long id,
        Long userId,
        BigDecimal balance
) {

    public static PointSearchResult fromEntity(Point point) {
        return new PointSearchResult(point.getId(), point.getUserId(), point.getBalance());
    }

}
