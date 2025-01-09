package kr.hhplus.be.server.interfaces.api.point.request;

import jakarta.validation.constraints.NotNull;

public record PointChargeRequest(
        @NotNull
        Long userId,
        int amount
) {
}
