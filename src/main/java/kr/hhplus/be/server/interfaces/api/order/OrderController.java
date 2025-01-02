package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @PostMapping
    public ApiResponse<Map<String, Object>> order(@RequestBody Map<String, Object> requestBody) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", 1);
        result.put("userId", 1);
        result.put("quantity", 1);
        result.put("couponId", 1);
        result.put("totalPrice", 10000);
        result.put("discountAmount", 1000);
        result.put("finalPrice", 9000);
        result.put("orderDate", "2024-01-01 10:00:00");
        result.put("orderStatus", "PENDING");

        return ApiResponse.ok(result, ResponseCode.SUCCESS_ORDER);
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> searchOrders(@RequestParam Long userId) {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> result1 = new HashMap<>();

        result1.put("id", 1);
        result1.put("userId", 1);
        result1.put("quantity", 1);
        result1.put("couponId", 1);
        result1.put("totalPrice", 10000);
        result1.put("discountAmount", 1000);
        result1.put("finalPrice", 9000);
        result1.put("orderDate", "2024-01-01 10:00:00");
        result1.put("orderStatus", "PENDING");

        Map<String, Object> result2 = new HashMap<>();

        result2.put("id", 2);
        result2.put("userId", 1);
        result2.put("quantity", 5);
        result2.put("couponId", 2);
        result2.put("totalPrice", 20000);
        result2.put("discountAmount", 10000);
        result2.put("finalPrice", 10000);
        result2.put("orderDate", "2024-01-01 10:00:00");
        result2.put("orderStatus", "PENDING");

        list.add(result1);
        list.add(result2);

        return ApiResponse.ok(list, ResponseCode.SUCCESS_SEARCH_ORDERS);
    }
}
