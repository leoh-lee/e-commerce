package kr.hhplus.be.server.infrastructures.external.redis;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CouponIssueProcessor extends RedisScanTemplate {
    private static final String KEY_DELIMITER = ":";
    private static final String ISSUED_COUPON_KEY_PREFIX = "coupons:issued:";

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCouponRepository userCouponRepository;

    public CouponIssueProcessor(RedisTemplate<String, Object> redisTemplate, CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        super(redisTemplate);
        this.redisTemplate = redisTemplate;
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Override
    protected void processMatchedKey(String key) {
        // Key에서 couponId 추출
        String[] split = key.split(KEY_DELIMITER);
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
            return;
        }

        // 발급 요청이 있는 경우 발급 가능한 쿠폰 개수만큼 시간순으로 조회(ZRANGE)
        Set<Object> usersForIssue = redisTemplate.opsForZSet().range(key, 0, issuableCount - 1);

        if (!ObjectUtils.isEmpty(usersForIssue)) {
            // 발급된 쿠폰 Set에 쿠폰 발급 저장
            redisTemplate.opsForSet().add(ISSUED_COUPON_KEY_PREFIX + couponId, usersForIssue.toArray());
            // 발급된 쿠폰 요청은 발급 요청 ZSet에서 제거
            redisTemplate.opsForZSet().removeRange(key, 0, issuableCount - 1);

            List<UserCoupon> userCoupons = usersForIssue.stream()
                    .map(userId -> new UserCoupon(((Number) userId).longValue(), couponId, UserCouponStatus.ISSUED, LocalDateTime.now().plusMonths(1), null))
                    .toList();

            userCouponRepository.saveAll(userCoupons);
        }
    }
}
