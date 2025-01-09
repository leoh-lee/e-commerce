package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.TopOrderProductDto;

import java.util.List;

public interface OrderProductRepository {
    void saveAll(List<OrderProduct> orderProducts);

    List<TopOrderProductDto> findTopOrderProducts(int topCount);
}
