package kr.hhplus.be.server.domain.order.dto;

public record OrderTopSearchResult(
        Long productId,
        int orderCount,
        int rank
) {

    public static OrderTopSearchResult from(TopOrderProductDto topOrderProductDto) {
        return new OrderTopSearchResult(
                topOrderProductDto.getProductId(),
                topOrderProductDto.getOrderCount(),
                topOrderProductDto.getRank()
        );
    }
}
