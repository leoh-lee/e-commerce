package kr.hhplus.be.server.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTopSearchResult {

    private Long productId;
    private int orderCount;
    private int rank;

    public static OrderTopSearchResult from(TopOrderProductDto topOrderProductDto) {
        return new OrderTopSearchResult(
                topOrderProductDto.getProductId(),
                topOrderProductDto.getOrderCount(),
                topOrderProductDto.getRank()
        );
    }
}
