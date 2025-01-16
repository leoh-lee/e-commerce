package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponApi {

    private final CouponFacade couponFacade;

    @Override
    @PostMapping
    public ApiResponse<CouponIssueResponse> issueCoupon(@RequestBody CouponIssueRequest couponIssueRequest) {
        return ApiResponse.ok(couponFacade.issueCoupon(couponIssueRequest), ResponseCode.SUCCESS_ISSUE_COUPON);
    }

    @Override
    @GetMapping
    public ApiResponse<List<UserCouponSearchResponse>> searchUserCoupons(@RequestParam("userId") Long userId) {
        return ApiResponse.ok(couponFacade.getUserCoupons(userId), ResponseCode.SUCCESS_SEARCH_USER_COUPON);
    }

    @Override
    @GetMapping("/available")
    public ApiResponse<List<AvailableCouponResponse>> searchAvailableCoupons(@RequestParam("userId") Long userId) {
        return ApiResponse.ok(couponFacade.getIssuableCoupons(userId), ResponseCode.SUCCESS_SEARCH_AVAILABLE_COUPON);
    }

}
