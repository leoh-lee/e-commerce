package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductSearchRequest;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class ProductFacadeTest {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        for (int i = 1; i <= 100; i++) {
            Product product = new Product("상품명" + i, i * 1000);
            productRepository.save(product);
            ProductStock productStock = new ProductStock(product, 0);
            productStockRepository.save(productStock);
        }
    }

    @Test
    @DisplayName("상품 목록을 조회한다.")
    void getProducts() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest("상품명", 5000, 10000);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ProductSearchResponse> products = productFacade.getProducts(searchRequest, pageable);

        // then
        assertThat(products.getContent())
                .extracting("name", "price")
                .containsExactly(
                        Tuple.tuple("상품명5", 5000),
                        Tuple.tuple("상품명6", 6000),
                        Tuple.tuple("상품명7", 7000),
                        Tuple.tuple("상품명8", 8000),
                        Tuple.tuple("상품명9", 9000),
                        Tuple.tuple("상품명10", 10000)
                );
    }

}