package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @PostMapping
    public ApiResponse<CouponIssueResponse> issueCoupon(@RequestBody CouponIssueRequest couponIssueRequest) {

        CouponIssueResponse result = new CouponIssueResponse(1L, "10% 할인 쿠폰", "PERCENTAGE", null, 10 );

        return ApiResponse.ok(result, ResponseCode.SUCCESS_ISSUE_COUPON);
    }

    @GetMapping
    public ApiResponse<List<UserCouponSearchResponse>> searchUserCoupons(@RequestParam Long userId) {

        List<UserCouponSearchResponse> result = List.of(
                new UserCouponSearchResponse(1L, "10% 할인 쿠폰", "PERCENTAGE", null, 10),
                new UserCouponSearchResponse(2L, "10,000원 할인 쿠폰", "FIXED", 10_000, null)
        );

        return ApiResponse.ok(result, ResponseCode.SUCCESS_SEARCH_USER_COUPON);
    }

    @GetMapping("/available")
    public ApiResponse<List<AvailableCouponResponse>> searchAvailableCoupons() {

        List<AvailableCouponResponse> result = List.of(
                new AvailableCouponResponse(1L, "10% 할인 쿠폰", "PERCENTAGE", null, 10),
                new AvailableCouponResponse(2L, "10,000원 할인 쿠폰", "FIXED", 10_000, null)
        );

        return ApiResponse.ok(result, ResponseCode.SUCCESS_SEARCH_AVAILABLE_COUPON);
    }

}
