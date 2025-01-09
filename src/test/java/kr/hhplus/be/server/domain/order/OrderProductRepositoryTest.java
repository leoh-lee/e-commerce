package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.config.jpa.QueryDslConfig;
import kr.hhplus.be.server.domain.order.dto.TopOrderProductDto;
import kr.hhplus.be.server.infrastructures.core.order.OrderProductRepositoryImpl;
import kr.hhplus.be.server.support.util.DateTimeProviderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({OrderProductRepositoryImpl.class, QueryDslConfig.class, DateTimeProviderImpl.class})
class OrderProductRepositoryTest {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Test
    @DisplayName("주문량이 많은 상위 상품을 조회한다.")
    @Sql(scripts = "/sql/insert_order_product_data.sql")
    void findTopOrderProducts() {
        // given
        int topCount = 3;

        // when
        List<TopOrderProductDto> topOrderProducts = orderProductRepository.findTopOrderProducts(topCount);
        // then
        assertThat(topOrderProducts).isNotEmpty();
        assertThat(topOrderProducts.size()).isEqualTo(3);


        for (int i = 0; i < topCount; i++) {
            assertThat(topOrderProducts.get(i).getRank()).isEqualTo(i + 1);
        }

        for (int i = 0; i < topCount; i++) {
            if (i + 1 == topCount) {
               break;
            }

            assertThat(topOrderProducts.get(i).getOrderCount()).isGreaterThan(topOrderProducts.get(i+1).getOrderCount());
        }
    }
}