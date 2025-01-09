package kr.hhplus.be.server.interfaces.api.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.api.payment.request.PaymentSearchRequest;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;

@Tag(name = "payment", description = "결제 API")
public interface PaymentApi {

    @Operation(summary = "주문을 결제한다", description = "주문을 결제한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상적으로 결제에 성공했습니다.", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"error\": \"UserNotFound\", \"message\": \"사용자를 찾을 수 없습니다.\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "결제 잔액이 부족합니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"error\": \"PaymentFailed\", \"message\": \"결제 잔액이 부족합니다.\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "결제 실패", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"error\": \"PaymentFailed\", \"message\": \"결제 실패\" }")))
    })
    @PostMapping
    ApiResponse<Void> payment(@RequestBody PaymentRequest requestBody);

    @Operation(summary = "결제 목록을 조회한다", description = "결제 목록을 조회한다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 목록 조회에 성공했습니다.", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "결제 목록 조회 실패", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"error\": \"PaymentSearchFailed\", \"message\": \"결제 목록 조회 실패\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"error\": \"UserNotFound\", \"message\": \"사용자를 찾을 수 없습니다.\" }")))
    })
    @GetMapping
    ApiResponse<PageResponse<PaymentSearchResponse>> searchPayments(PaymentSearchRequest paymentSearchRequest);

}
