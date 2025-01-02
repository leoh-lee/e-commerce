package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping
    public ApiResponse<Map<String, Object>> payment(@RequestBody Map<String, Object> requestBody) {
        return ApiResponse.ok(ResponseCode.SUCCESS_PAYMENT);
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> searchPayments(@RequestParam Long userId) {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> result1 = new HashMap<>();
        result1.put("id", 1);
        result1.put("orderId", 1);
        result1.put("userId", 1);
        result1.put("paymentPrice", 10000);
        result1.put("paymentDate", "2024-01-01 10:00:00");
        result1.put("paymentStatus", "SUCCESS");
        result1.put("orderDate", "2024-01-01 10:00:00");
        result1.put("orderStatus", "PENDING");

        Map<String, Object> result2 = new HashMap<>();
        result2.put("id", 2);
        result2.put("orderId", 2);
        result2.put("userId", 1);
        result2.put("paymentPrice", 20000);
        result2.put("paymentDate", "2024-01-01 11:00:00");
        result2.put("paymentStatus", "SUCCESS");
        result2.put("orderDate", "2024-01-01 11:00:00");
        result2.put("orderStatus", "PENDING");

        list.add(result1);
        list.add(result2);

        return ApiResponse.ok(list, ResponseCode.SUCCESS_SEARCH_PAYMENT);
    }

}
