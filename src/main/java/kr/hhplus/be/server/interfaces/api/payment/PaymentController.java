package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentResponse;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentFacade paymentFacade;

    @Override
    @PostMapping
    public ApiResponse<PaymentResponse> payment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse payment = paymentFacade.payment(paymentRequest);
        return ApiResponse.ok(payment, ResponseCode.SUCCESS_PAYMENT);
    }

    @Override
    @GetMapping
    public ApiResponse<List<PaymentSearchResponse>> searchPayments(@RequestParam Long userId) {
        return ApiResponse.ok(paymentFacade.searchPaymentByUserId(userId), ResponseCode.SUCCESS_SEARCH_PAYMENT);
    }

}
