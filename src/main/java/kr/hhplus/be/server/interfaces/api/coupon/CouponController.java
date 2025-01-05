package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @PostMapping
    public ApiResponse<Map<String, Object>> issueCoupon(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", 1);
        result.put("name", "10% 할인 쿠폰");
        result.put("type", "PERCENTAGE");
        result.put("discountRate", 10);

        return ApiResponse.ok(result, ResponseCode.SUCCESS_ISSUE_COUPON);
    }

    @GetMapping
    public ApiResponse<List<Object>> searchUserCoupons(@RequestParam Long userId) {
        List<Object> list = new ArrayList<>();

        Map<String, Object> result1 = new HashMap<>();

        result1.put("id", 1);
        result1.put("name", "10% 할인 쿠폰");
        result1.put("type", "PERCENTAGE");
        result1.put("discountRate", 10);

        Map<String, Object> result2 = new HashMap<>();

        result2.put("id", 2);
        result2.put("name", "10,000원 할인 쿠폰");
        result2.put("type", "FIXED");
        result2.put("discountAmount", 10000);

        list.add(result1);
        list.add(result2);

        return ApiResponse.ok(list, ResponseCode.SUCCESS_SEARCH_USER_COUPON);
    }

    @GetMapping("/available")
    public ApiResponse<List<Object>> searchAvailableCoupons() {
        List<Object> list = new ArrayList<>();

        Map<String, Object> result1 = new HashMap<>();

        result1.put("id", 1);
        result1.put("name", "10% 할인 쿠폰");
        result1.put("type", "PERCENTAGE");
        result1.put("discountRate", 10);

        Map<String, Object> result2 = new HashMap<>();

        result2.put("id", 2);
        result2.put("name", "10,000원 할인 쿠폰");
        result2.put("type", "FIXED");
        result2.put("discountAmount", 10000);

        list.add(result1);
        list.add(result2);

        return ApiResponse.ok(list, ResponseCode.SUCCESS_SEARCH_AVAILABLE_COUPON);
    }

}
