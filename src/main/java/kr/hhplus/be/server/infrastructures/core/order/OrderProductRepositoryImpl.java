package kr.hhplus.be.server.infrastructures.core.order;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderProductRepository;
import kr.hhplus.be.server.domain.order.QOrderProduct;
import kr.hhplus.be.server.domain.order.dto.TopOrderProductDto;
import kr.hhplus.be.server.domain.product.QProduct;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

    private final OrderProductJpaRepository orderProductJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final DateTimeProvider dateTimeProvider;

    @Override
    public void saveAll(List<OrderProduct> orderProducts) {
        orderProductJpaRepository.saveAll(orderProducts);
    }

    @Override
    public List<TopOrderProductDto> findTopOrderProducts(int topCount) {
        LocalDateTime threeDaysAgo = dateTimeProvider.getLocalDateTimeNow().minusDays(3);

        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QProduct product = QProduct.product;

        List<TopOrderProductDto> topOrderProductDtos = queryFactory.select(
                        Projections.constructor(TopOrderProductDto.class,
                                orderProduct.productId.as("productId"),
                                orderProduct.quantity.sum().as("orderCount")
                        )
                )
                .from(orderProduct)
                .where(orderProduct.createdAt.after(threeDaysAgo))
                .groupBy(orderProduct.productId)
                .orderBy(orderProduct.quantity.sum().desc())
                .limit(topCount)
                .fetch();

        AtomicInteger rank = new AtomicInteger(1);
        topOrderProductDtos.forEach(it -> it.setRank(rank.getAndIncrement()));

        return topOrderProductDtos;
    }

}
