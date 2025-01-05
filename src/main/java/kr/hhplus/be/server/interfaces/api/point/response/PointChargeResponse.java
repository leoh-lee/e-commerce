package kr.hhplus.be.server.interfaces.api.point.response;

public record PointChargeResponse(
        Long userId,
        int amount
) {
}
