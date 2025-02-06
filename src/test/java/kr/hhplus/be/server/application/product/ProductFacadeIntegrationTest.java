package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.application.product.dto.ProductSearchRequest;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import kr.hhplus.be.server.support.util.PageWrapper;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProductFacadeIntegrationTest extends IntegrationTest {

    private static final String PRODUCTS_CACHE_KEY_PREFIX = "products:default:";

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("상품 목록을 조회한다.")
    void getProducts() {
        // given
        for (int i = 1; i <= 100; i++) {
            Product product = new Product("상품명" + i, BigDecimal.valueOf(i * 1000));
            productRepository.save(product);
            ProductStock productStock = new ProductStock(product, 0);
            productStockRepository.save(productStock);
        }

        ProductSearchRequest searchRequest = new ProductSearchRequest("상품명", BigDecimal.valueOf(5_000), BigDecimal.valueOf(10_000));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ProductSearchResponse> products = productFacade.getProducts(searchRequest, pageable);

        // then
        assertThat(products.getContent())
                .extracting(ProductSearchResponse::name, product -> product.price().intValue())
                .containsExactly(
                        Tuple.tuple("상품명5", 5_000),
                        Tuple.tuple("상품명6", 6_000),
                        Tuple.tuple("상품명7", 7_000),
                        Tuple.tuple("상품명8", 8_000),
                        Tuple.tuple("상품명9", 9_000),
                        Tuple.tuple("상품명10",10_000)
                );
    }

    @Test
    @DisplayName("Redis에 캐시된 상품 목록이 있는 경우 캐시를 조회한다.")
    void getProducts_whenCached() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        String key = PRODUCTS_CACHE_KEY_PREFIX + pageable.getPageNumber() + ":" + pageable.getPageSize();

        List<ProductSearchResponse> cachedData = List.of(
                new ProductSearchResponse(1L, "상품1", BigDecimal.valueOf(10_000), 10),
                new ProductSearchResponse(2L, "상품2", BigDecimal.valueOf(20_000), 20),
                new ProductSearchResponse(3L, "상품3", BigDecimal.valueOf(30_000), 30),
                new ProductSearchResponse(4L, "상품4", BigDecimal.valueOf(40_000), 40)
        );

        Page<ProductSearchResponse> cachedPageData = new PageImpl<>(cachedData);

        PageWrapper<ProductSearchResponse> cachedWrapperData = new PageWrapper<>(cachedPageData);

        redisTemplate.opsForValue().set(key, cachedWrapperData);

        ProductSearchRequest searchRequest = new ProductSearchRequest(null, null, null);

        // when
        Page<ProductSearchResponse> products = productFacade.getProducts(searchRequest, pageable);

        // then
        assertThat(products.getContent())
                .extracting(ProductSearchResponse::name, product -> product.price().intValue())
                .containsExactly(
                        Tuple.tuple("상품1", 10_000),
                        Tuple.tuple("상품2", 20_000),
                        Tuple.tuple("상품3", 30_000),
                        Tuple.tuple("상품4", 40_000)
                );
    }

}