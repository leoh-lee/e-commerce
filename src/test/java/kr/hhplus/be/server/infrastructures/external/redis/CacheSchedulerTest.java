package kr.hhplus.be.server.infrastructures.external.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;
import kr.hhplus.be.server.support.TestConfig;
import kr.hhplus.be.server.support.TestDataBuilder;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static kr.hhplus.be.server.infrastructures.external.redis.CacheScheduler.PRODUCTS_TOP_5_CACHE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
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
    private TestDataBuilder testDataBuilder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserCouponRepository userCouponRepository;

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

        assertThat(orderTopSearchResults).extracting("productId", "orderCount", "rank")
                .containsExactly(
                        tuple(1L, 50, 1),
                        tuple(2L, 40, 2),
                        tuple(3L, 30, 3),
                        tuple(4L, 20, 4),
                        tuple(5L, 10, 5)
                );
    }


    @Test
    @DisplayName("선착순 쿠폰 발급 요청을 배치로 처리한다.")
    void testCouponBatchProcess() {
        // given
        Coupon coupon = testDataBuilder.createCoupon("쿠폰1", CouponType.FIXED, 30, 10_000, null);
        long couponId = coupon.getId();
        String couponKey = "coupons:requests:" + couponId;
        String issuedCouponKey = "coupons:issued:" + couponId;

        // 쿠폰 발급 요청을 Redis에 추가
        redisTemplate.opsForZSet().add(couponKey, 1001L, System.currentTimeMillis());
        redisTemplate.opsForZSet().add(couponKey, 1002L, System.currentTimeMillis() + 1);
        redisTemplate.expire(couponKey, 10, TimeUnit.MINUTES);

        // when
        cacheScheduler.issueCouponBatch();

        // then
        // 발급된 쿠폰이 Redis에 저장되었는지 확인
        Set<Object> issuedCoupons = redisTemplate.opsForSet().members(issuedCouponKey);
        assertThat(issuedCoupons).contains(1001, 1002);

        // 원래 요청한 발급 목록이 삭제되었는지 확인
        Set<Object> remainingRequests = redisTemplate.opsForZSet().range(couponKey, 0, -1);
        assertThat(remainingRequests).isEmpty();

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(1001L);
        assertThat(userCoupons).hasSize(1);
        assertThat(userCoupons.getFirst().getCouponId()).isEqualTo(couponId);

        List<UserCoupon> userCoupons2 = userCouponRepository.findByUserId(1002L);
        assertThat(userCoupons2).hasSize(1);
        assertThat(userCoupons2.getFirst().getCouponId()).isEqualTo(couponId);
    }

}
