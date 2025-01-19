package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.domain.order.exception.OrderNotFoundException;
import kr.hhplus.be.server.domain.payment.exception.PaymentNotFoundException;
import kr.hhplus.be.server.domain.point.exception.PointLimitExceededException;
import kr.hhplus.be.server.domain.point.exception.PointNotFoundException;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.interfaces.api.point.response.PointChargeResponse;
import kr.hhplus.be.server.interfaces.api.point.response.PointSearchResponse;
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
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@Import(PageableTestConfig.class)
@WebMvcTest(controllers = PointController.class)
class PointControllerTest {

    private static final String BASE_URL = "/api/v1/points";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointFacade pointFacade;

    @ParameterizedTest
    @MethodSource("notFoundExceptions")
    @DisplayName("포인트 충전 시 자원이 존재하지 않으면 404 상태코드를 반환하고, 자원 조회 실패 코드가 body에 담긴다.")
    void chargePoint_whenResourceNotExists_returnStatus404(ResourceNotFoundException resourceException) throws Exception {
        // given
        doThrow(resourceException).when(pointFacade).chargePoint(any());

        // when
        // then
        ResponseCode responseCode = resourceException.getResponseCode();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "amount": 10000
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
    @DisplayName("포인트 충전 시 잔액 초과인 경우 400 상태코드를 반환한다.")
    void chargePoint_whenLimitExceeded_returnStatus400() throws Exception {
        // given
        doThrow(PointLimitExceededException.class).when(pointFacade).chargePoint(any());

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "amount": 10000
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.MAXIMUM_POINT_LIMIT_EXCEEDED.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.MAXIMUM_POINT_LIMIT_EXCEEDED.getMessage()));
    }

    @Test
    @DisplayName("포인트 충전 성공 시 200 상태코드를 반환한다.")
    void chargePoint_whenSuccess_returnStatus400() throws Exception {
        // given
        when(pointFacade.chargePoint(any())).thenReturn(new PointChargeResponse(1L, BigDecimal.valueOf(10_000)));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "amount": "10000"
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_CHARGE_POINT.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_CHARGE_POINT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.amount").value(10_000))
        ;
    }

    @Test
    @DisplayName("포인트 조회 시 200 상태코드를 반환한다.")
    void searchPoint_whenSuccess_returnStatus200() throws Exception {
        // given
        when(pointFacade.searchPoint(any())).thenReturn(new PointSearchResponse(1L, BigDecimal.valueOf(10_000)));

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_SEARCH_USER_POINT.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_USER_POINT.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.userId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.balance").value(10_000))
        ;
    }

}