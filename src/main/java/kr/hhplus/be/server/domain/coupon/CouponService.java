package kr.hhplus.be.server.domain.coupon;

import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.domain.coupon.dto.CouponSearchResult;
import kr.hhplus.be.server.domain.coupon.dto.CouponUseResult;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponDto;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponSearchResult;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.exception.AlreadyIssuedCouponException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotUsableException;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    public CouponSearchResult getCouponById(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponNotFoundException::new);
        return CouponSearchResult.fromEntity(coupon);
    }

    @Transactional
    public void issueCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponNotFoundException::new);

        if (!coupon.isUsable(dateTimeProvider.getLocalDateTimeNow())) {
            throw new CouponNotUsableException();
        }

        coupon.decreaseStock();
        UserCoupon userCoupon = new UserCoupon(userId, couponId, UserCouponStatus.ISSUED, dateTimeProvider.getLocalDateTimeNow(), null);

        try {
            userCouponRepository.save(userCoupon);
        } catch (ConstraintViolationException e) {
            throw new AlreadyIssuedCouponException();
        }
    }

    @Transactional
    public CouponUseResult useCoupon(Long userCouponId, int price) {
        UserCouponDto userCouponDto = userCouponRepository.findByIdWithCoupon(userCouponId);

        UserCoupon userCoupon = userCouponDto.userCoupon();
        Coupon coupon = userCouponDto.coupon();

        int discountPrice = coupon.getDiscountPrice(price);
        userCoupon.changeUseStatus(dateTimeProvider.getLocalDateTimeNow());

        return CouponUseResult.of(userCoupon, price, discountPrice);
    }

}
