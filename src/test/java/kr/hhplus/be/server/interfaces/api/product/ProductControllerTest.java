package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.support.http.response.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    private static final String BASE_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("전체 상품 리스트를 조회한다.")
    void searchProducts_ReturnsProductList() throws Exception {
        // given
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ResponseCode.SUCCESS_SEARCH_PRODUCTS.getMessage()))
        ;
    }

}