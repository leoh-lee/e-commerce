package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;
import kr.hhplus.be.server.domain.product.exception.StockNotEnoughException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCreateDto;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.dto.UserCreateResult;
import kr.hhplus.be.server.interfaces.api.order.request.OrderProductsRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.support.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderFacadeConcurrencyTest extends IntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataBuilder testDataBuilder;

    private Long userId;
    private Product product;

    @BeforeEach
    void setUp() {
        UserCreateResult user = userService.createUser(new UserCreateDto("테스트 사용자"));
        userId = user.userId();

        product = new Product("테스트 상품", BigDecimal.valueOf(10000));
        ProductStock stock = new ProductStock(product, 10);
        product.addStock(stock);
        productRepository.save(product);
    }

    @Test
    @DisplayName("여러 명의 사용자가 동시에 동일 상품을 주문하면 재고가 정확하게 차감된다.")
    void order_multiple_users_concurrent_requests() throws InterruptedException {
        // given
        int numberOfThreads = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            User user = testDataBuilder.createUser("user" + (i + 1));
            executorService.execute(() -> {
                try {
                    OrderRequest orderRequest = new OrderRequest(
                            user.getId(),
                            List.of(
                                    new OrderProductsRequest(product.getId(), 2)
                            ),
                            null
                    );

                    orderFacade.order(orderRequest);
                } catch (StockNotEnoughException ignored) {
                    // 재고 부족으로 실패한 경우
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        ProductSearchResult updatedProduct = productService.searchProduct(product.getId());

        assertThat(updatedProduct.stock()).isZero();
    }

    // TODO: TransactionId를 넣어서 따닥에 대한 방어 필요
    @Test
    @DisplayName("동시에 여러 주문 요청이 들어오더라도 재고가 정확하게 차감된다.")
    void order_concurrent_requests() throws InterruptedException {
        // given
        int numberOfThreads = 5;
        Product product2 = testDataBuilder.createProduct("상품2", 30_000, 10);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        OrderRequest orderRequest = new OrderRequest(
                userId,
                List.of(
                        new OrderProductsRequest(product.getId(), 2),
                        new OrderProductsRequest(product2.getId(), 2)
                ),
                null
        );

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    orderFacade.order(orderRequest);
                } catch (StockNotEnoughException ignored) {
                    // 재고 부족으로 실패한 경우
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        ProductSearchResult updatedProduct = productService.searchProduct(product.getId());
        ProductSearchResult updatedProduct2 = productService.searchProduct(product2.getId());

        assertThat(updatedProduct.stock()).isZero();
        assertThat(updatedProduct2.stock()).isZero();
    }
}