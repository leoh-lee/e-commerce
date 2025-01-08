package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록을 조회한다.")
    void searchProducts_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        List<Product> products = List.of(
                new Product("상품1", 10000),
                new Product("상품2", 20000)
        );

        when(productRepository.findAll(any(), any())).thenReturn(new PageImpl<>(products));

        // when
        ProductSearchDto productSearchDto = new ProductSearchDto("상품1", 10000, 30000);

        Page<ProductSearchResult> result = productService.searchProducts(productSearchDto, pageable);

        // then
        assertThat(result.getContent()).hasSize(products.size());
    }

    @Test
    @DisplayName("상품 단 건 조회 시 해당 상품이 없는 경우 예외가 발상한다.")
    void searchProduct_whenNotFound_throwsProductNotFoundException() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> productService.searchProduct(1L));
    }

    @Test
    @DisplayName("상품을 단 건 조회한다.")
    void searchProduct_success() {
        // given
        String productName = "상품1";
        int productPrice = 10000;

        Optional<Product> product = Optional.of(new Product(productName, productPrice));

        when(productRepository.findById(anyLong())).thenReturn(product);

        // when
        ProductSearchResult productSearchResult = productService.searchProduct(1L);

        // then
        assertThat(productSearchResult.productName()).isEqualTo(productName);
        assertThat(productSearchResult.productPrice()).isEqualTo(productPrice);
    }

}