package kr.hhplus.be.server.domain.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TopOrderProductDto {
    private Long productId;
    private int orderCount;
    @Setter
    private int rank;

    public TopOrderProductDto(Long productId, int orderCount) {
        this.productId = productId;
        this.orderCount = orderCount;
    }
}
