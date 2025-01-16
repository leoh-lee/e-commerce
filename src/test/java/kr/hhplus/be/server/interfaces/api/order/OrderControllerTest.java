package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.domain.order.exception.OrderNotFoundException;
import kr.hhplus.be.server.domain.payment.exception.PaymentNotFoundException;
import kr.hhplus.be.server.domain.point.exception.PointNotFoundException;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderTopSearchResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@Import(PageableTestConfig.class)
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    private static final String BASE_URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderFacade orderFacade;

    @ParameterizedTest
    @MethodSource("notFoundExceptions")
    @DisplayName("주문 시 자원이 존재하지 않으면 404 상태코드를 반환하고, 자원 조회 실패 코드가 body에 담긴다.")
    void order_whenResourceNotExists_returnStatus404(ResourceNotFoundException resourceException) throws Exception {
        // given
        doThrow(resourceException).when(orderFacade).order(any());

        // when
        // then
        ResponseCode responseCode = resourceException.getResponseCode();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                          {
                            "userId": 1,
                            "products": [
                              {
                                  "productId": 1,
                                  "quantity": 10
                              }
                            ],
                            "userCouponId": 1
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
    @DisplayName("주문이 성공하면 200 상태코드를 반환한다.")
    void order_whenSuccess_returnsStatus200() throws Exception {
        // given
        when(orderFacade.order(any())).thenReturn(new OrderResponse(1L, 1L, 1L, BigDecimal.valueOf(10_000), BigDecimal.valueOf(5_000), BigDecimal.valueOf(5_000)));
        ResponseCode successOrder = ResponseCode.SUCCESS_ORDER;

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                  {
                                    "userId": 1,
                                    "products": [],
                                    "userCouponId": 1
                                  }
                                """
                        )
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(successOrder.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(successOrder.getMessage()));
    }

    @Test
    @DisplayName("주문 조회에 성공하면 200 상태코드를 반환한다.")
    void searchOrders_whenSuccess_returnsStatus200() throws Exception {
        // given
        when(orderFacade.getOrdersByUserId(any(), any())).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));
        ResponseCode successOrder = ResponseCode.SUCCESS_SEARCH_ORDERS;

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("page", "0")
                        .param("size", "5")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(successOrder.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(successOrder.getMessage()));
    }

    @Test
    @DisplayName("상위 주문 상품 목록을 조회한다.")
    void searchProductsTop_whenSuccess_ReturnsProductList() throws Exception {
        // given
        List<OrderTopSearchResponse> responses = List.of(
                new OrderTopSearchResponse(1L, 5, 1),
                new OrderTopSearchResponse(2L, 3, 2),
                new OrderTopSearchResponse(3L, 1, 3)
        );
        when(orderFacade.searchTopOrder(anyInt())).thenReturn(responses);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/top")
                        .param("topCount", "3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_SEARCH_TOP_ORDERS.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_TOP_ORDERS.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].orderCount").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].rank").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].productId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].orderCount").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[1].rank").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].productId").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].orderCount").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[2].rank").value(3));
    }

}
