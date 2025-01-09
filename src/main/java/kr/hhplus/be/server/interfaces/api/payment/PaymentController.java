package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentSearchRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentApi {

    @Override
    public ApiResponse<Void> payment(@RequestBody PaymentRequest requestBody) {
        return ApiResponse.ok(ResponseCode.SUCCESS_PAYMENT);
    }

    @Override
    public ApiResponse<PageResponse<PaymentSearchResponse>> searchPayments(PaymentSearchRequest paymentSearchRequest) {

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

        Page<PaymentSearchResponse> pageResult = new PageImpl<>(result);

        return ApiResponse.ok(new PageResponse<>(pageResult), ResponseCode.SUCCESS_SEARCH_PAYMENT);
    }

}
