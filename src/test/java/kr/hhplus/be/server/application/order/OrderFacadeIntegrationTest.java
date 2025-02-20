package kr.hhplus.be.server.application.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.infrastructures.external.kafka.order.producer.OrderEventProducer;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.Outbox;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxRepository;
import kr.hhplus.be.server.infrastructures.external.kafka.outbox.OutboxStatus;
import kr.hhplus.be.server.interfaces.api.order.response.OrderSearchResponse;
import kr.hhplus.be.server.support.TestDataPlatform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponSearchResult;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderProductDto;
import kr.hhplus.be.server.domain.order.dto.OrderSearchResult;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.ProductSearchResult;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.product.exception.StockNotEnoughException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.interfaces.api.order.request.OrderProductsRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import kr.hhplus.be.server.support.IntegrationTest;
import kr.hhplus.be.server.support.TestDataBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.*;

@Import(OrderEventProducer.class)
class OrderFacadeIntegrationTest extends IntegrationTest{

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private DataPlatform dataPlatform;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @Autowired
    private OutboxRepository outboxRepository;

    private User user;

    private Product product;

    @BeforeEach
    void setUp() {
        product = testDataBuilder.createProduct("상품1", 10_000, 1);
        user = testDataBuilder.createUser("유저1");
    }

    @Test
    @DisplayName("주문 시 사용자가 존재하지 않으면 실패한다.")
    void order_whenUserNotFound_throwsUserNotFoundException() {
        // given
        long notExistsUserId = 999L;
        OrderRequest orderRequest = createOrderRequest(notExistsUserId, List.of(
                new OrderProductsRequest(1L, 1)
        ));

        // when // then
        assertThatThrownBy(() -> orderFacade.order(orderRequest))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("주문 시 상품이 존재하지 않으면 주문이 실패한다.")
    void order_whenProductNotFound_throwsProductNotFoundException() {
        // given
        long notExistsProductId = 999L;

        OrderRequest orderRequest = createOrderRequest(user.getId(), List.of(
                new OrderProductsRequest(notExistsProductId, 1)
        ));

        // when // then
        assertThatThrownBy(() -> orderFacade.order(orderRequest))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @ParameterizedTest
    @CsvSource({"5, 1", "10, 3", "6, 1", "55, 30"})
    @DisplayName("주문 시 상품의 주문 수량만큼 상품의 재고가 차감한다.")
    void order_thenDecreaseProductStock(int baseStock, int orderStock) {
        // given
        Product createdProduct = testDataBuilder.createProduct("상품", 30_000, baseStock);
        OrderRequest orderRequest = createOrderRequest(user.getId(), List.of(new OrderProductsRequest(createdProduct.getId(), orderStock)));

        // when
        orderFacade.order(orderRequest);

        ProductSearchResult productSearchResult = productService.searchProduct(createdProduct.getId());

        // then
        assertThat(productSearchResult.stock()).isEqualTo(baseStock - orderStock);
    }

    @ParameterizedTest
    @CsvSource({"5, 6", "10, 11", "6, 7", "55, 56"})
    @DisplayName("주문 시 상품의 재고보다 주문량이 많으면 실패한다.")
    void order_whenExceedOrder_throwsStockNotEnoughException(int baseStock, int orderStock) {
        // given
        Product createdProduct = testDataBuilder.createProduct("상품", 30_000, baseStock);
        OrderRequest orderRequest = createOrderRequest(user.getId(), List.of(new OrderProductsRequest(createdProduct.getId(), orderStock)));
        
        // when
        // then
        assertThatThrownBy(() -> orderFacade.order(orderRequest))
                .isInstanceOf(StockNotEnoughException.class);
                
        assertThat(productService.searchProduct(createdProduct.getId()).stock()).isEqualTo(baseStock);
    }
    
    @Test
    @DisplayName("주문 시 적용한 정률 쿠폰이 있으면 쿠폰 적용가로 주문된다.")
    void order_whenExistsCoupon_whenExistsDiscountRateCoupon_thenDiscountedFinalPrice() {
        // given
        Product product2 = testDataBuilder.createProduct("상품2", 20_000, 10);

        Coupon coupon = testDataBuilder.createCoupon("10% 할인쿠폰", CouponType.PERCENTAGE, 1, null, 10);
        UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon.getId(), LocalDateTime.now().plusDays(5));

        List<OrderProductsRequest> orderProductsRequests = List.of(
                new OrderProductsRequest(product.getId(), 1),   // 10_000
                new OrderProductsRequest(product2.getId(), 3)   // 60_000
        );

        OrderRequest orderRequest = createOrderRequestWithCoupon(user.getId(), userCoupon.getId(), orderProductsRequests);

        // when
        OrderResponse orderResponse = orderFacade.order(orderRequest);

        // then
        assertThat(orderResponse.basePrice()).isEqualByComparingTo(BigDecimal.valueOf(70_000));
        assertThat(orderResponse.discountAmount()).isEqualByComparingTo(BigDecimal.valueOf(7_000));
        assertThat(orderResponse.finalPrice()).isEqualByComparingTo(BigDecimal.valueOf(63_000));
    }

    @Test
    @DisplayName("주문 시 적용한 정액 쿠폰이 있으면 쿠폰 적용가로 주문된다.")
    void order_whenExistsCoupon_whenExistsFixedCoupon_thenDiscountedFinalPrice() {
        // given
        Product product2 = testDataBuilder.createProduct("상품2", 20_000, 10);

        Coupon coupon = testDataBuilder.createCoupon("10,000원 할인쿠폰", CouponType.FIXED, 1, 10_000, null);
        UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon.getId(), LocalDateTime.now().plusDays(5));

        List<OrderProductsRequest> orderProductsRequests = List.of(
                new OrderProductsRequest(product.getId(), 1),   // 10_000
                new OrderProductsRequest(product2.getId(), 3)   // 60_000
        );

        OrderRequest orderRequest = createOrderRequestWithCoupon(user.getId(), userCoupon.getId(), orderProductsRequests);

        // when
        OrderResponse orderResponse = orderFacade.order(orderRequest);

        // then
        assertThat(orderResponse.basePrice()).isEqualByComparingTo(BigDecimal.valueOf(70_000));
        assertThat(orderResponse.discountAmount()).isEqualByComparingTo(BigDecimal.valueOf(10_000));
        assertThat(orderResponse.finalPrice()).isEqualByComparingTo(BigDecimal.valueOf(60_000));
    }

    @Test
    @DisplayName("주문 시 쿠폰을 사용하면 사용자 쿠폰 상태가 USED로 변경된다.")
    void order_whenExistsCoupon_whenUserCouponUsed_thenUserCouponStatusChanged() {
        // given
        Coupon coupon = testDataBuilder.createCoupon("10,000원 할인쿠폰", CouponType.FIXED, 1, 10_000, null);
        UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon.getId(), LocalDateTime.now().plusDays(5));

        List<OrderProductsRequest> orderProductsRequests = List.of(
                new OrderProductsRequest(product.getId(), 1)
        );

        OrderRequest orderRequest = createOrderRequestWithCoupon(user.getId(), userCoupon.getId(), orderProductsRequests);

        // when
        OrderResponse orderResponse = orderFacade.order(orderRequest);

        // then
        List<UserCouponSearchResult> userCoupons = couponService.getUserCoupons(userCoupon.getId());

        UserCouponSearchResult userCouponSearchResult = userCoupons.get(0);
        assertThat(userCouponSearchResult.userCouponStatus()).isEqualTo(UserCouponStatus.USED);
    }

    @Test
    @DisplayName("주문 시 쿠폰이 없으면 상품 가격 그대로 주문히 진행된다.")
    void order_whenNotExistsCoupon_thenFinalPriceNotChanged() {
        // given
        List<OrderProductsRequest> orderProductsRequests = List.of(
                new OrderProductsRequest(product.getId(), 1)
        );

        List<OrderProductDto> orderProductDtos = orderProductsRequests.stream()
                .map(OrderProductsRequest::toDto)
                .toList();

        BigDecimal totalPriceBy = productService.getTotalPriceBy(orderProductDtos);

        OrderRequest orderRequest = createOrderRequest(user.getId(), orderProductsRequests);

        // when
        OrderResponse orderResponse = orderFacade.order(orderRequest);

        // then
        assertThat(orderResponse.finalPrice()).isEqualByComparingTo(totalPriceBy);
    }

    @Test
    @DisplayName("주문이 성공하면 주문이 저장되고 데이터 플랫폼에 이력을 전송한다.")
    void order_success() {
        // given
        Coupon coupon = testDataBuilder.createCoupon("10,000원 할인쿠폰", CouponType.FIXED, 1, 10_000, null);
        UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon.getId(), LocalDateTime.now().plusDays(5));

        List<OrderProductsRequest> orderProductsRequests = List.of(
                new OrderProductsRequest(product.getId(), 1)
        );

        OrderRequest orderRequest = createOrderRequestWithCoupon(user.getId(), userCoupon.getId(), orderProductsRequests);
        TestDataPlatform testDataPlatform = (TestDataPlatform) dataPlatform;
        Integer platformSentCount = testDataPlatform.getSentCount();

        // when
        OrderResponse orderResponse = orderFacade.order(orderRequest);

        Long orderId = orderResponse.id();

        OrderSearchResult findOrder = orderService.getOrderById(orderId);

        // then
        assertThat(findOrder)
                .extracting("id", "userId", "userCouponId")
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(orderResponse.id(), user.getId(), userCoupon.getId());

        try {
            Thread.sleep(2000);
            assertThat(testDataPlatform.getSentCount()).isEqualTo(platformSentCount + 1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("주문이 성공하면 Outbox에 이벤트가 저장된다.")
    void order_whenSuccess_thenSaveOutboxWithPending() {
        // given
        Coupon coupon = testDataBuilder.createCoupon("10,000원 할인쿠폰", CouponType.FIXED, 1, 10_000, null);
        UserCoupon userCoupon = testDataBuilder.createUserCoupon(user.getId(), coupon.getId(), LocalDateTime.now().plusDays(5));

        List<OrderProductsRequest> orderProductsRequests = List.of(
                new OrderProductsRequest(product.getId(), 1)
        );

        OrderRequest orderRequest = createOrderRequestWithCoupon(user.getId(), userCoupon.getId(), orderProductsRequests);

        // when
        OrderResponse orderResponse = orderFacade.order(orderRequest);

        // then
        Long orderId = orderResponse.id();
        List<Outbox> orderCreateOutbox = outboxRepository.findByTopicContainingAndStatus("order_create", OutboxStatus.SUCCESS);

        assertThat(orderCreateOutbox).isNotEmpty();
        assertThat(orderCreateOutbox.getFirst().getAggregateId()).isEqualTo(String.valueOf(orderId));
    }

    @Test
    @DisplayName("사용자 ID로 주문을 조회한다.")
    void getOrdersByUserId_whenSuccess() {
        // given
        Order order1 = testDataBuilder.createOrder(user.getId(), null, 30000);
        Order order2 = testDataBuilder.createOrder(user.getId(), null, 60000);

        // when
        Page<OrderSearchResponse> orders = orderFacade.getOrdersByUserId(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(orders.getContent().getFirst().id()).isEqualTo(order1.getId());
        assertThat(orders.getContent().getLast().id()).isEqualTo(order2.getId());
    }
    
    private OrderRequest createOrderRequest(Long userId, List<OrderProductsRequest> products) {
        return new OrderRequest(userId, products, null);
    }

    private OrderRequest createOrderRequestWithCoupon(Long userId, Long userCouponId,
                                                      List<OrderProductsRequest> products) {
        return new OrderRequest(userId, products, userCouponId);
    }

}
