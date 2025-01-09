package kr.hhplus.be.server.interfaces.api.point.response;

import kr.hhplus.be.server.domain.point.dto.PointSearchResult;

public record PointSearchResponse(
        Long userId,
        int balance
) {

    public static PointSearchResponse from(PointSearchResult searchResult) {
        return new PointSearchResponse(searchResult.userId(), searchResult.balance());
    }

}
