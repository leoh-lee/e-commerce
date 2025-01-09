package kr.hhplus.be.server.domain.point.dto;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;

public record PointSearchResult(
        Long id,
        Long userId,
        int balance
) {

    public static PointSearchResult fromEntity(Point point) {
        User user = point.getUser();
        return new PointSearchResult(point.getId(), user.getId(), point.getBalance());
    }

}
