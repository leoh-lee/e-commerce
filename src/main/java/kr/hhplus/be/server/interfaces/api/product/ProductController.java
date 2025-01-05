package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    @GetMapping
    public ApiResponse<List<Object>> searchProducts() {
        List<Object> list = new ArrayList<>();

        Map<String, Object> result1 = new HashMap<>();
        result1.put("id", 1);
        result1.put("name", "상품1");
        result1.put("price", 10000);
        result1.put("stock", 100);

        Map<String, Object> result2 = new HashMap<>();
        result2.put("id", 2);
        result2.put("name", "상품2");
        result2.put("price", 20000);
        result2.put("stock", 200);

        list.add(result1);
        list.add(result2);

        return ApiResponse.ok(list, ResponseCode.SUCCESS_SEARCH_USER_POINT);
    }

    @GetMapping("/top")
    public ApiResponse<List<Object>> searchProductsTop5() {
        List<Object> list = new ArrayList<>();

        Map<String, Object> result1 = new HashMap<>();
        result1.put("rank", 1);
        result1.put("id", 1);
        result1.put("name", "상품1");
        result1.put("price", 10000);
        result1.put("stock", 100);

        Map<String, Object> result2 = new HashMap<>();
        result2.put("rank", 2);
        result2.put("id", 2);
        result2.put("name", "상품2");
        result2.put("price", 20000);
        result2.put("stock", 200);

        Map<String, Object> result3 = new HashMap<>();
        result3.put("rank", 3);
        result3.put("id", 3);
        result3.put("name", "상품3");
        result3.put("price", 20000);
        result3.put("stock", 200);

        Map<String, Object> result4 = new HashMap<>();
        result4.put("rank", 4);
        result4.put("id", 4);
        result4.put("name", "상품4");
        result4.put("price", 20000);
        result4.put("stock", 200);

        Map<String, Object> result5 = new HashMap<>();
        result5.put("rank", 5);
        result5.put("id", 5);
        result5.put("name", "상품5");
        result5.put("price", 20000);
        result5.put("stock", 200);

        list.add(result1);
        list.add(result2);
        list.add(result3);
        list.add(result4);
        list.add(result5);

        return ApiResponse.ok(list, ResponseCode.SUCCESS_SEARCH_TOP_ORDERS);
    }

}
