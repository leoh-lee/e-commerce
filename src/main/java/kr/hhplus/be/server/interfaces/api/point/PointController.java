package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    @PostMapping
    public ApiResponse<Map<String, Object>> chargePoint(@RequestBody Map<String, String> requestBody) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", 1L);
        result.put("amount", 10_000);

        return ApiResponse.ok(result, ResponseCode.SUCCESS_CHARGE_POINT);
    }

    @GetMapping("/{userId}")
    public ApiResponse<Map<String, Object>> searchPoint(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", 1L);
        result.put("balance", 10_000);

        return ApiResponse.ok(result, ResponseCode.SUCCESS_SEARCH_USER_POINT);
    }

}
