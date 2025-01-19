package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 단 건 조회 시 해당 상품이 없는 경우 예외가 발상한다.")
    void searchProduct_whenNotFound_throwsProductNotFoundException() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> productService.searchProduct(1L));
    }

}