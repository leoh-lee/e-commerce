package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.config.annotation.DistributedLock;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponIssueResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponFacade {

    public static final String COUPONS_ISSUE_REQUESTS_KEY_PREFIX = "coupons:requests:";

    private final UserService userService;
    private final CouponService couponService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @DistributedLock(key = "'issue_coupon_'.concat(#couponIssueRequest.couponId())")
    @Transactional
    public CouponIssueResponse issueCoupon(CouponIssueRequest couponIssueRequest) {
        Long userId = couponIssueRequest.userId();
        Long couponId = couponIssueRequest.couponId();

        log.info("Issue Coupon >>> userId: {}, couponId : {}", userId, couponId);

        if (!userService.existsById(userId)) {
            throw new UserNotFoundException();
        }

        CouponIssueResult couponIssueResult = couponService.issueCoupon(userId, couponId);

        dataPlatform.send(new DataPlatformSendRequest<>(userId, RequestType.COUPON_ISSUE, dateTimeProvider.getLocalDateTimeNow(), couponIssueResult));

        return CouponIssueResponse.from(couponIssueResult);
    }

    public void issueCouponAsync(CouponIssueRequest couponIssueRequest) {
        redisTemplate.opsForZSet().add(COUPONS_ISSUE_REQUESTS_KEY_PREFIX + couponIssueRequest.couponId(), couponIssueRequest.userId(), dateTimeProvider.getCurrentTimestamp());
    }

    @Transactional(readOnly = true)
    public List<UserCouponSearchResponse> getUserCoupons(Long userId) {
        return couponService.getUserCoupons(userId)
                .stream()
                .map(UserCouponSearchResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvailableCouponResponse> getIssuableCoupons(Long userId) {
        return couponService.getIssuableCoupons(userId)
                .stream()
                .map(AvailableCouponResponse::from)
                .toList();
    }

}
