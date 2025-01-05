package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentSearchRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping
    public ApiResponse<Void> payment(@RequestBody PaymentRequest requestBody) {
        return ApiResponse.ok(ResponseCode.SUCCESS_PAYMENT);
    }

    @GetMapping
    public ApiResponse<List<PaymentSearchResponse>> searchPayments(PaymentSearchRequest paymentSearchRequest) {

        List<PaymentSearchResponse> result = List.of(
                new PaymentSearchResponse(
                        1L,
                        1L,
                        paymentSearchRequest.userId(),
                        10_000,
                        "CONFIRMED",
                        "2024-01-05",
                        "2024-01-05",
                        "COMPLETE"
                ),
                new PaymentSearchResponse(
                        2L,
                        2L,
                        paymentSearchRequest.userId(),
                        20_000,
                        "CANCELED",
                        "2024-01-05",
                        "2024-01-05",
                        "CANCELED"
                )
        );

        return ApiResponse.ok(result, ResponseCode.SUCCESS_SEARCH_PAYMENT);
    }

}
