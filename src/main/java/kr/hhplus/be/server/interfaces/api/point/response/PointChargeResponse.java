package kr.hhplus.be.server.interfaces.api.point.response;

import kr.hhplus.be.server.domain.point.dto.PointChargeResult;

import java.math.BigDecimal;

public record PointChargeResponse(
        Long userId,
        BigDecimal amount
) {

    public static PointChargeResponse from(PointChargeResult pointChargeResult) {
        return new PointChargeResponse(pointChargeResult.userId(), pointChargeResult.balance());
    }
}
