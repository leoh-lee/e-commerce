package kr.hhplus.be.server.interfaces.api.point.response;

import kr.hhplus.be.server.domain.point.dto.PointSearchResult;

import java.math.BigDecimal;

public record PointSearchResponse(
        Long userId,
        BigDecimal balance
) {

    public static PointSearchResponse from(PointSearchResult searchResult) {
        return new PointSearchResponse(searchResult.userId(), searchResult.balance());
    }

}
