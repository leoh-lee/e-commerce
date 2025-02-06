package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.product.dto.ProductSearchRequest;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.api.product.response.ProductSearchResponse;
import kr.hhplus.be.server.support.util.PageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductFacade {

    private static final String PRODUCTS_CACHE_KEY_PREFIX = "products:default:";

    private final ProductService productService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public Page<ProductSearchResponse> getProducts(ProductSearchRequest searchRequest, Pageable pageable) {
        if (isEmptySearchCondition(searchRequest)) {
            Page<ProductSearchResponse> cachedProducts = getCachedProducts(pageable);

            if (cachedProducts != null) {
                return cachedProducts;
            }
        }

        Page<ProductSearchResponse> products = productService.searchProducts(searchRequest.toSearchDto(), pageable)
                .map(ProductSearchResponse::from);

        if (isEmptySearchCondition(searchRequest)) {
            cacheProducts(products, pageable);
        }

        return products;
    }

    private boolean isEmptySearchCondition(ProductSearchRequest searchRequest) {
        return searchRequest == null || searchRequest.productName() == null && searchRequest.maxPrice() == null && searchRequest.minPrice() == null;
    }

    private Page<ProductSearchResponse> getCachedProducts(Pageable pageable) {
        String key = PRODUCTS_CACHE_KEY_PREFIX + pageable.getPageNumber() + ":" + pageable.getPageSize();

        log.info("getProducts Redis Key >>> {}", key);
        PageWrapper<ProductSearchResponse> cachedPage = (PageWrapper<ProductSearchResponse>) redisTemplate.opsForValue().get(key);
        return cachedPage.toPage(pageable.getPageNumber(), pageable.getPageSize());
    }

    private void cacheProducts(Page<ProductSearchResponse> products, Pageable pageable) {
        String key = PRODUCTS_CACHE_KEY_PREFIX + pageable.getPageNumber() + ":" + pageable.getPageSize();

        PageWrapper<ProductSearchResponse> pageWrapper = new PageWrapper<>(products);
        redisTemplate.opsForValue().set(key, pageWrapper, Duration.ofMinutes(10));
    }

}
