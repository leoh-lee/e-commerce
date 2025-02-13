package kr.hhplus.be.server.support;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderPrice;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructures.core.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructures.core.coupon.UserCouponJpaRepository;
import kr.hhplus.be.server.infrastructures.core.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructures.core.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructures.core.product.ProductJpaRepository;
import kr.hhplus.be.server.infrastructures.core.product.ProductStockJpaRepository;
import kr.hhplus.be.server.infrastructures.core.user.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional
public class TestDataBuilder {
    private final UserJpaRepository userJpaRepository;
    private final PointJpaRepository pointJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductStockJpaRepository productStockJpaRepository;
    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;
    private final OrderJpaRepository orderJpaRepository;

    public Product createProduct(String name, int price, int stock) {
        Product savedProduct = productJpaRepository.save(
                new Product(
                        name, BigDecimal.valueOf(price)
                )
        );

        productStockJpaRepository.save(new ProductStock(savedProduct, stock));

        return savedProduct;
    }

    public User createUser(String name) {
        return userJpaRepository.save(new User(name));
    }

    public Point createPoint(Long userId, BigDecimal balance) {
        Point point = new Point(userId, balance);
        return pointJpaRepository.save(point);
    }

    public Coupon createCoupon(String name, CouponType couponType, int couponStock, Integer discountAmount, Integer discountRate) {
        CouponInfo couponInfo = new CouponInfo(name, couponType, couponStock);
        DiscountInfo discountInfo = new DiscountInfo(discountAmount, discountRate);
        CouponUsableDate couponUsableDate = new CouponUsableDate(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));

        Coupon coupon = new Coupon(couponInfo, discountInfo, couponUsableDate);

        return couponJpaRepository.save(coupon);
    }

    public UserCoupon createUserCoupon(Long userId, Long couponId, LocalDateTime expiredDate) {
        return userCouponJpaRepository.save(new UserCoupon(userId, couponId, UserCouponStatus.ISSUED, expiredDate, null));
    }

    public Order createOrder(Long userId, Long userCouponId, int finalPrice) {
        OrderPrice orderPrice = new OrderPrice(BigDecimal.valueOf(10_000), BigDecimal.ZERO, BigDecimal.valueOf(finalPrice));
        return orderJpaRepository.save(new Order(userId, userCouponId, orderPrice, OrderStatus.ORDERED));
    }
}