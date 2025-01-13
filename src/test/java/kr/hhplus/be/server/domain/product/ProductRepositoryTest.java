package kr.hhplus.be.server.domain.product;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.support.RepositoryTest;
import kr.hhplus.be.server.domain.product.dto.ProductSearchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class ProductRepositoryTest extends RepositoryTest {

    private static final int PAGE_SIZE = 10;

    private Pageable pageable;

    @Autowired
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, PAGE_SIZE);

        for (int i = 1; i <= 5; i++) {
            Product product = new Product("신발" + i, BigDecimal.valueOf(i * 10000));
            productRepository.save(product);
        }

        for (int i = 6; i <= 10; i++) {
            Product product = new Product("상의" + i, BigDecimal.valueOf(i * 10000));
            productRepository.save(product);
        }

        em.flush();
    }

    @Test
    @DisplayName("상품 이름으로 상품 목록을 like 조회한다.")
    void findAll_withProductName() {
        // given
        ProductSearchDto productSearchDto = new ProductSearchDto("신발", null, null);

        // when
        Page<Product> result = productRepository.findAll(productSearchDto, pageable);
        List<Product> resultContent = result.getContent();

        // then
        assertThat(resultContent).hasSize(5);
        assertThat(resultContent.getFirst().getProductName()).isEqualTo("신발1");
    }

    @Test
    @DisplayName("금액 범위로 상품 목록을 조회한다.")
    void findAll_withPriceRange() {
        // given
        ProductSearchDto productSearchDto = new ProductSearchDto(null, BigDecimal.valueOf(10_000), BigDecimal.valueOf(30_000));

        // when
        Page<Product> result = productRepository.findAll(productSearchDto, pageable);
        List<Product> resultContent = result.getContent();

        // then
        assertThat(resultContent).hasSize(3);
        assertThat(resultContent).extracting("productPrice")
                .containsExactly(BigDecimal.valueOf(10_000), BigDecimal.valueOf(20_000), BigDecimal.valueOf(30_000));
    }

    @Test
    @DisplayName("상품명과 금액 범위로 상품 목록을 조회한다.")
    void findAll_withProductNameANdPriceRange() {
        // given
        ProductSearchDto productSearchDto = new ProductSearchDto("신발", BigDecimal.valueOf(10_000), BigDecimal.valueOf(80_000));

        // when
        Page<Product> result = productRepository.findAll(productSearchDto, pageable);
        List<Product> resultContent = result.getContent();

        // then
        assertThat(resultContent).hasSize(5);
        assertThat(resultContent).extracting("productName", "productPrice")
                .containsExactly(
                    tuple("신발1", BigDecimal.valueOf(10000)),
                        tuple("신발2", BigDecimal.valueOf(20000)),
                        tuple("신발3", BigDecimal.valueOf(30000)),
                        tuple("신발4", BigDecimal.valueOf(40000)),
                        tuple("신발5", BigDecimal.valueOf(50000))
                );
    }

    @Test
    @DisplayName("상품을 단 건 조회한다.")
    void findById_success() {
        // given
        String productName = "상품1";
        BigDecimal productPrice = BigDecimal.valueOf(10_000);

        Product product = new Product(productName, productPrice);
        em.persist(product);
        em.flush();

        // when
        Product findProduct = productRepository.findById(product.getId()).get();

        // then
        assertThat(findProduct.getProductName()).isEqualTo(productName);
        assertThat(findProduct.getProductPrice()).isEqualTo(productPrice);
    }

}