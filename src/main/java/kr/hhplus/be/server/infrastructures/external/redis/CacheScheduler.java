package kr.hhplus.be.server.infrastructures.external.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderTopSearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static kr.hhplus.be.server.application.coupon.CouponFacade.COUPONS_ISSUE_REQUESTS_KEY_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheScheduler {

    public static final int TOP_COUNT = 5;
    public static final String PRODUCTS_TOP_5_CACHE_KEY = "products:top5";

    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CouponIssueProcessor couponIssueProcessor;

    // 1일전 ~ 3일전 주문 상품 중 주문량이 가장 많은 상품 조회하여 캐싱
    // 매일 1시에 동작
    @Scheduled(cron = "0 0 1 ? * * *")
    public List<OrderTopSearchResult> cacheTopOrderProducts() {
        List<OrderTopSearchResult> topOrders = orderService.getTopOrders(TOP_COUNT);

        try {
            String topOrdersString = objectMapper.writeValueAsString(topOrders);
            redisTemplate.opsForValue().set(PRODUCTS_TOP_5_CACHE_KEY, topOrdersString, Duration.ofHours(25));

            return topOrders;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 5000L)
    public void issueCouponBatch() {
        String keyPattern = COUPONS_ISSUE_REQUESTS_KEY_PREFIX + "*";
        couponIssueProcessor.scanAndProcess(keyPattern);
    }
}
