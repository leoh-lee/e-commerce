package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import kr.hhplus.be.server.support.PageableTestConfig;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(PageableTestConfig.class)
@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    private static final String BASE_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductFacade productFacade;

    @Test
    @DisplayName("전체 상품 리스트를 조회 성공 시 상태코드 200을 반환한다.")
    void searchProducts_ReturnsProductList() throws Exception {
        // given
        Page<ProductSearchResponse> productSearchResponses = new PageImpl<>(List.of(
                new ProductSearchResponse(1L, "상품1", BigDecimal.valueOf(10_000), 10),
                new ProductSearchResponse(2L, "상품2", BigDecimal.valueOf(20_000), 20)
        ), PageRequest.of(0, 10), 2
        );

        when(productFacade.getProducts(any(), any())).thenReturn(productSearchResponses);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("productName", "test")
                        .param("minPrice", "10000")
                        .param("maxPrice", "30000")
                        .param("page", "1")
                        .param("size", "10")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.SUCCESS_SEARCH_PRODUCTS.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_PRODUCTS.getMessage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[0].name").value("상품1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[0].price").value(10_000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[0].stock").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[1].name").value("상품2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[1].price").value(20_000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.items[1].stock").value(20))
        ;
    }

}