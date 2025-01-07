package kr.hhplus.be.server.domain.point;

public record PointChargeResult(
        Long id,
        Long userId,
        int balance
) {

    public static PointChargeResult fromEntity(Point point) {
        return new PointChargeResult(point.getId(), point.getUser().getId(), point.getBalance());
    }

}
