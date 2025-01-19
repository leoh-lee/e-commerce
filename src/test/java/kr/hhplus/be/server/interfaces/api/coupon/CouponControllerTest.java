package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotUsableException;
import kr.hhplus.be.server.domain.order.exception.OrderNotFoundException;
import kr.hhplus.be.server.domain.payment.exception.PaymentNotFoundException;
import kr.hhplus.be.server.domain.point.exception.PointNotFoundException;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.interfaces.api.coupon.response.AvailableCouponResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.api.coupon.response.UserCouponSearchResponse;
import kr.hhplus.be.server.support.PageableTestConfig;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(CouponController.class)
@Import(PageableTestConfig.class)
class CouponControllerTest {

    private static final String BASE_URL = "/api/v1/coupons";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponFacade couponFacade;

    @ParameterizedTest
    @MethodSource("notFoundExceptions")
    @DisplayName("쿠폰 발급 시 자원이 존재하지 않으면 404 상태코드를 반환하고, 자원 조회 실패 코드가 body에 담긴다.")
    void issueCoupon_whenResourceNotExists_returnStatus404(ResourceNotFoundException resourceException) throws Exception {
        // given
        doThrow(resourceException).when(couponFacade).issueCoupon(any());

        // when
        // then
        ResponseCode responseCode = resourceException.getResponseCode();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "couponId": 1
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(responseCode.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseCode.getMessage()));
    }

    static Stream<Arguments> notFoundExceptions() {
        return Stream.of(
                Arguments.arguments(new UserNotFoundException()),
                Arguments.arguments(new ProductNotFoundException()),
                Arguments.arguments(new PointNotFoundException()),
                Arguments.arguments(new OrderNotFoundException()),
                Arguments.arguments(new PaymentNotFoundException())

        );
    }

    @Test
    @DisplayName("쿠폰 발급 시 유효하지 않은 쿠폰이면 500 상태코드가 반환된다.")
    void issueCoupon_whenUnusableCoupon_returnStatus500() throws Exception {
        // given
        doThrow(CouponNotUsableException.class).when(couponFacade).issueCoupon(any());

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "couponId": 1
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.COUPON_INVALID.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.COUPON_INVALID.getMessage()));
    }

    @Test
    @DisplayName("쿠폰 발급이 성공하면 200 상태코드를 반환한다.")
    void issueCoupon_whenSuccess_returnsStatus200() throws Exception {
        // given
        when(couponFacade.issueCoupon(any())).thenReturn(new CouponIssueResponse(1L, "쿠폰1", "FIXED", 1000, null));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "couponId": 1
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_ISSUE_COUPON.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_ISSUE_COUPON.getMessage()));
    }

    @Test
    @DisplayName("사용자 쿠폰 조회에 성공하면 200 상태코드를 반환한다.")
    void getUserCoupons_whenSuccess_returnsStatus200() throws Exception {
        // given
        when(couponFacade.getUserCoupons(any())).thenReturn(List.of(
                new UserCouponSearchResponse(1L, "쿠폰1", "ISSUED", 10_000, null),
                new UserCouponSearchResponse(2L, "쿠폰2", "ISSUED", 20_000, null),
                new UserCouponSearchResponse(3L, "쿠폰3", "ISSUED", 30_000, null)
        ));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_SEARCH_USER_COUPON.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_USER_COUPON.getMessage()));
    }

    @Test
    @DisplayName("발급 가능한 쿠폰 조회 시 성공하면 200 상태코드를 반환한다.")
    void searchAvailableCoupons_whenSuccess_returnsStatus200() throws Exception {
        // given
        when(couponFacade.getIssuableCoupons(any())).thenReturn(List.of(
                new AvailableCouponResponse(1L, "쿠폰1", "ISSUED", 10_000, null),
                new AvailableCouponResponse(2L, "쿠폰2", "ISSUED", 20_000, null),
                new AvailableCouponResponse(3L, "쿠폰3", "ISSUED", 30_000, null)
        ));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_SEARCH_AVAILABLE_COUPON.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_AVAILABLE_COUPON.getMessage()));
    }

}