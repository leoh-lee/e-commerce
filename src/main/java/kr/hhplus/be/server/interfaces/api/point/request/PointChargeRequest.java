package kr.hhplus.be.server.interfaces.api.point.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PointChargeRequest(
        @NotNull
        Long userId,
        BigDecimal amount
) {
}
