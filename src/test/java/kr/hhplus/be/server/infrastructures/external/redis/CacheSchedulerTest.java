package kr.hhplus.be.server.infrastructures.external.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;
import kr.hhplus.be.server.support.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static kr.hhplus.be.server.infrastructures.external.redis.CacheScheduler.PRODUCTS_TOP_5_CACHE_KEY;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Import(TestConfig.class)
class CacheSchedulerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private CacheScheduler cacheScheduler;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상위 주문 상품을 캐싱한다.")
    void cacheTopOrderProducts() throws JsonProcessingException {
        // given
        when(orderService.getTopOrders(anyInt())).thenReturn(List.of(
                new OrderTopSearchResult(1L, 50, 1),
                new OrderTopSearchResult(2L, 40, 2),
                new OrderTopSearchResult(3L, 30, 3),
                new OrderTopSearchResult(4L, 20, 4),
                new OrderTopSearchResult(5L, 10, 5)
        ));

        // when
        cacheScheduler.cacheTopOrderProducts();

        // then
        String cachedDataJson = (String) redisTemplate.opsForValue().get(PRODUCTS_TOP_5_CACHE_KEY);

        List<OrderTopSearchResult> orderTopSearchResults = objectMapper.readValue(cachedDataJson, new TypeReference<List<OrderTopSearchResult>>() {
        });

        Assertions.assertThat(orderTopSearchResults).extracting("productId", "orderCount", "rank")
                .containsExactly(
                        tuple(1L, 50, 1),
                        tuple(2L, 40, 2),
                        tuple(3L, 30, 3),
                        tuple(4L, 20, 4),
                        tuple(5L, 10, 5)
                );
    }

}
