package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.domain.order.exception.OrderNotFoundException;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.exception.PaymentNotFoundException;
import kr.hhplus.be.server.domain.point.exception.PointNotEnoughException;
import kr.hhplus.be.server.domain.point.exception.PointNotFoundException;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentResponse;
import kr.hhplus.be.server.interfaces.api.payment.response.PaymentSearchResponse;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@Import(PageableTestConfig.class)
@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    private static final String BASE_URL = "/api/v1/payments";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentFacade paymentFacade;

    @ParameterizedTest
    @MethodSource("notFoundExceptions")
    @DisplayName("결제 시 자원이 존재하지 않으면 404 상태코드를 반환하고, 자원 조회 실패 코드가 body에 담긴다.")
    void payment_whenResourceNotExists_returnStatus404(ResourceNotFoundException resourceException) throws Exception {
        // given
        doThrow(resourceException).when(paymentFacade).payment(any());

        // when
        // then
        ResponseCode responseCode = resourceException.getResponseCode();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "orderId": 1
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
    @DisplayName("결제 시 포인트가 충분하지 않으면 상태코드 400을 반환한다.")
    void payment_whenNotEnoughPoint_returnStatus400() throws Exception {
        // given
        doThrow(PointNotEnoughException.class).when(paymentFacade).payment(any());

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "orderId": 1
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.POINT_NOT_ENOUGH.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.POINT_NOT_ENOUGH.getMessage()));
    }

    @Test
    @DisplayName("결제 성공 시 상태코드 200을 반환한다.")
    void payment_whenSuccess_returnStatus200() throws Exception {
        // given
        when(paymentFacade.payment(any())).thenReturn(new PaymentResponse(1L, 1L, BigDecimal.valueOf(10_000), PaymentStatus.COMPLETED));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "orderId": 1
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_PAYMENT.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_PAYMENT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.orderId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.paymentPrice").value(10_000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.paymentStatus").value(PaymentStatus.COMPLETED.name()))
        ;
    }

    @Test
    @DisplayName("결제 조회 성공 시 상태코드 200을 반환한다.")
    void searchPayment_whenSuccess_returnStatus200() throws Exception {
        // given
        when(paymentFacade.searchPaymentsByUserId(any())).thenReturn(List.of(
                new PaymentSearchResponse(1L, 1L, 1L, BigDecimal.valueOf(10_000), PaymentStatus.COMPLETED),
                new PaymentSearchResponse(2L, 2L, 1L, BigDecimal.valueOf(20_000), PaymentStatus.PENDING)
        ));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_SEARCH_PAYMENT.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_PAYMENT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].orderId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].paymentPrice").value(10_000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].paymentStatus").value(PaymentStatus.COMPLETED.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].orderId").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].paymentPrice").value(20_000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].paymentStatus").value(PaymentStatus.PENDING.name()))
        ;
    }


}