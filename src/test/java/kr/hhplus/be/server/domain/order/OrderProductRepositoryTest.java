package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import kr.hhplus.be.server.support.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.order.dto.TopOrderProductDto;

class OrderProductRepositoryTest extends RepositoryTest {

    @Autowired
    private OrderProductRepository orderProductRepository;
    
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("주문량이 많은 상위 상품을 조회한다.")
    void findTopOrderProducts() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);
        
        // 테스트 데이터 삽입
        insertTestData(threeDaysAgo);

        // when
        int topCount = 3;
        List<TopOrderProductDto> topOrderProducts = orderProductRepository.findTopOrderProducts(topCount);

        // then
        assertThat(topOrderProducts).isNotEmpty();
        assertThat(topOrderProducts.size()).isEqualTo(3);

        for (int i = 0; i < topCount; i++) {
            assertThat(topOrderProducts.get(i).getRank()).isEqualTo(i + 1);
        }

        for (int i = 0; i < topCount - 1; i++) {
            assertThat(topOrderProducts.get(i).getOrderCount())
                .isGreaterThanOrEqualTo(topOrderProducts.get(i+1).getOrderCount());
        }
        
        // 예상 순위 검증
        assertThat(topOrderProducts.get(0).getProductId()).isEqualTo(1L); // 1위
        assertThat(topOrderProducts.get(1).getProductId()).isEqualTo(2L); // 2위
        assertThat(topOrderProducts.get(2).getProductId()).isEqualTo(3L); // 3위
    }

    private void insertTestData(LocalDateTime threeDaysAgo) {
        String sql = """
            INSERT INTO order_product (product_id, quantity, created_at, updated_at)
            VALUES
            -- 1위가 될 상품 (총 주문량 80)
            (1, 50, :date1, :date1),
            (1, 30, :date2, :date2),
            
            -- 2위가 될 상품 (총 주문량 60)
            (2, 40, :date3, :date3),
            (2, 20, :date4, :date4),
            
            -- 3위가 될 상품 (총 주문량 50)
            (3, 35, :date5, :date5),
            (3, 15, :date6, :date6),
            
            -- 집계에서 제외될 오래된 데이터
            (4, 100, :date7, :date7),
            (4, 100, :date8, :date8)
            """;

        em.createNativeQuery(sql)
            .setParameter("date1", threeDaysAgo.plusHours(1))
            .setParameter("date2", threeDaysAgo.plusHours(2))
            .setParameter("date3", threeDaysAgo.plusHours(3))
            .setParameter("date4", threeDaysAgo.plusHours(4))
            .setParameter("date5", threeDaysAgo.plusHours(5))
            .setParameter("date6", threeDaysAgo.plusHours(6))
            .setParameter("date7", threeDaysAgo.minusDays(1))
            .setParameter("date8", threeDaysAgo.minusDays(2))
            .executeUpdate();
            
        em.flush();
    }
}
