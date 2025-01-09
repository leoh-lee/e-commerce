package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponIssueResult;
import kr.hhplus.be.server.domain.coupon.dto.CouponSearchResult;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponSearchResult;
import kr.hhplus.be.server.domain.user.UserSearchResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.RequestType;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.util.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponFacade {

    private final UserService userService;
    private final CouponService couponService;
    private final DataPlatform dataPlatform;
    private final DateTimeProvider dateTimeProvider;

    @Transactional
    public CouponIssueResponse issueCoupon(CouponIssueRequest couponIssueRequest) {
        Long userId = couponIssueRequest.userId();

        UserSearchResult userById = userService.getUserById(userId);

        CouponIssueResult couponIssueResult = couponService.issueCoupon(userId, couponIssueRequest.couponId());

        dataPlatform.send(new DataPlatformSendRequest<>(userId, RequestType.COUPON_ISSUE, dateTimeProvider.getLocalDateTimeNow(), couponIssueResult));

        return CouponIssueResponse.from(couponIssueResult);
    }

    public List<UserCouponSearchResponse> getUserCoupons(Long userId) {
        return couponService.getUserCoupons(userId)
                .stream()
                .map(UserCouponSearchResponse::from)
                .toList();
    }

    public List<AvailableCouponResponse> getIssuableCoupons(Long userId) {
        return couponService.getIssuableCoupons(userId)
                .stream()
                .map(AvailableCouponResponse::from)
                .toList();
    }

}
