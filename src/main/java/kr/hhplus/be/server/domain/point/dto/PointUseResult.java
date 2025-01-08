package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.Point;

public record PointUseResult(
        Long id,
        Long userId,
        int balance
) {

    public static PointUseResult fromEntity(Point point) {
        return new PointUseResult(point.getId(), point.getUser().getId(), point.getBalance());
    }

}
