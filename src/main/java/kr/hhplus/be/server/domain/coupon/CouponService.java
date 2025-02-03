package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.config.annotation.DistributedLock;
import kr.hhplus.be.server.domain.coupon.dto.*;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotUsableException;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CouponService {

    private final DateTimeProvider dateTimeProvider;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public List<CouponSearchResult> getIssuableCoupons(Long userId) {
        LocalDateTime now = dateTimeProvider.getLocalDateTimeNow();

        List<Coupon> coupons = couponRepository.findAll();

        Map<Long, UserCoupon> userCouponMap = userCouponRepository.findByUserId(userId)
                .stream()
                .collect(Collectors.toMap(UserCoupon::getCouponId, Function.identity()));

        return coupons.stream()
                .filter(coupon -> !userCouponMap.containsKey(coupon.getId()))
                .filter(coupon -> coupon.isUsable(now))
                .map(CouponSearchResult::fromEntity)
                .toList();
    }

    public List<UserCouponSearchResult> getUserCoupons(Long userId) {
        return userCouponRepository.findByUserIdWithCoupon(userId)
                .stream()
                .map(UserCouponSearchResult::from)
                .toList();
    }

    @Transactional
    public CouponIssueResult issueCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponNotFoundException::new);

        if (!coupon.isUsable(dateTimeProvider.getLocalDateTimeNow())) {
            throw new CouponNotUsableException();
        }

        coupon.decreaseStock();

        UserCoupon userCoupon = new UserCoupon(userId, couponId, UserCouponStatus.ISSUED, dateTimeProvider.getLocalDateTimeNow(), null);
        userCouponRepository.save(userCoupon);

        return new CouponIssueResult(
                couponId,
                userId,
                coupon.getCouponInfo().getCouponName(),
                coupon.getCouponInfo().getCouponType(),
                coupon.getDiscountInfo().getDiscountAmount(),
                coupon.getDiscountInfo().getDiscountRate()
        );
    }

    @Transactional
    public CouponUseResult useCoupon(Long userCouponId, BigDecimal price) {
        UserCouponDto userCouponDto = userCouponRepository.findByIdWithCoupon(userCouponId);

        UserCoupon userCoupon = userCouponDto.userCoupon();
        Coupon coupon = userCouponDto.coupon();

        BigDecimal discountPrice = coupon.getDiscountPrice(price);
        userCoupon.changeUseStatus(dateTimeProvider.getLocalDateTimeNow());

        return CouponUseResult.of(userCoupon, price, discountPrice);
    }

}
