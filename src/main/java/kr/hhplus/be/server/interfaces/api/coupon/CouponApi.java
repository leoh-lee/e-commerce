package kr.hhplus.be.server.interfaces.api.coupon;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.api.coupon.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.PageResponse;

@Tag(name = "coupon", description = "쿠폰 API")
public interface CouponApi {

    @Operation(summary = "쿠폰을 발급한다.", description = "쿠폰을 발급한다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "쿠폰을 성공적으로 발급했습니다", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"error\": \"User Not Found\", \"message\": \"사용자를 찾을 수 없습니다.\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "만료된 쿠폰입니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5200\", \"message\": \"만료된 쿠폰입니다.\" }"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 쿠폰입니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5201\", \"message\": \"유효하지 않은 쿠폰입니다.\" }"))),
    })
    @PostMapping
    ApiResponse<CouponIssueResponse> issueCoupon(@RequestBody CouponIssueRequest couponIssueRequest);

    @Operation(summary = "사용자 쿠폰 목록을 조회한다.", description = "사용자 쿠폰 목록을 조회한다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 쿠폰 목록 조회에 성공했습니다.", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5500\", \"message\": \"사용자를 찾을 수 없습니다.\" }")))
    })
    @GetMapping
    ApiResponse<PageResponse<UserCouponSearchResponse>> searchUserCoupons(@RequestParam Long userId);

    @Operation(summary = "발급 가능한 쿠폰 목록을 조회한다.", description = "발급 가능한 쿠폰 목록을 조회한다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "발급 가능한 쿠폰 목록 조회에 성공했습니다", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.", content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = "{ \"code\": \"5500\", \"message\": \"사용자를 찾을 수 없습니다.\" }")))
    })
    @GetMapping("/available")
    ApiResponse<PageResponse<AvailableCouponResponse>> searchAvailableCoupons();

}
