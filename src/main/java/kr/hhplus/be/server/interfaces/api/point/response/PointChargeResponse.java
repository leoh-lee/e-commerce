package kr.hhplus.be.server.interfaces.api.point.response;

import kr.hhplus.be.server.domain.point.dto.PointChargeResult;

public record PointChargeResponse(
        Long userId,
        int amount
) {

    public static PointChargeResponse from(PointChargeResult pointChargeResult) {
        return new PointChargeResponse(pointChargeResult.userId(), pointChargeResult.balance());
    }
}
