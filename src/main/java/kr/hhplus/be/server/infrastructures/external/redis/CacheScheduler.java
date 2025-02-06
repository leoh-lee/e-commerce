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
    private static final String KEY_DELIMITER = ":";
    public static final String ISSUED_COUPON_KEY_PREFIX = "coupons:issued:";

    private final OrderService orderService;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

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
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            return;
        }

        try (RedisConnection connection = connectionFactory.getConnection()) {
            String keyPattern = COUPONS_ISSUE_REQUESTS_KEY_PREFIX + "*";
            ScanOptions scanOptions = ScanOptions.scanOptions().match(keyPattern).build();
            Cursor<byte[]> cursor = connection.keyCommands().scan(scanOptions);

            while (cursor.hasNext()) {
                // 다음 Key를 가져옴 (쿠폰 발급)
                byte[] next = cursor.next();
                String matchedKey = new String(next, StandardCharsets.UTF_8);

                // Key에서 couponId 추출
                String[] split = matchedKey.split(KEY_DELIMITER);
                String couponIdStr = split[split.length - 1];
                long couponId = Long.parseLong(couponIdStr);

                // Coupon ID로 쿠폰 재고 조회
                Coupon coupon = couponRepository.findById(couponId).orElseThrow();
                int couponStock = coupon.getCouponInfo().getCouponStock();

                // 이미 발급된 쿠폰과 발급 요청이 들어온 개수 조회
                Long issuedCount = redisTemplate.opsForZSet().zCard(ISSUED_COUPON_KEY_PREFIX + couponId);

                // 발급 가능한 쿠폰 개수 계산 (전체 쿠폰 개수 - 발급된 쿠폰 수)
                long issuableCount = couponStock - (ObjectUtils.isEmpty(issuedCount) ? 0 : issuedCount);

                // 발급 요청이 없는 경우 continue
                if (issuableCount < 1) {
                    continue;
                }

                // 발급 요청이 있는 경우 발급 가능한 쿠폰 개수만큼 시간순으로 조회(ZRANGE)
                Set<Object> usersForIssue = redisTemplate.opsForZSet().range(matchedKey, 0, issuableCount - 1);

                if (!ObjectUtils.isEmpty(usersForIssue)) {
                    // 발급된 쿠폰 Set에 쿠폰 발급 저장
                    redisTemplate.opsForSet().add(ISSUED_COUPON_KEY_PREFIX + couponId, usersForIssue.toArray());
                    // 발급된 쿠폰 요청은 발급 요청 ZSet에서 제거
                    redisTemplate.opsForZSet().removeRange(matchedKey, 0, issuableCount - 1);

                    List<UserCoupon> userCoupons = usersForIssue.stream()
                            .map(userId -> new UserCoupon(((Number) userId).longValue(), couponId, UserCouponStatus.ISSUED, LocalDateTime.now().plusMonths(1), null))
                            .toList();

                    userCouponRepository.saveAll(userCoupons);
                }
            }
        }

    }
}
