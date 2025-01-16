package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.order.exception.AlreadyPayedOrderException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long userCouponId;

    @Embedded
    private OrderPrice orderPrice;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(Long userId, Long userCouponId, OrderPrice orderPrice, OrderStatus orderStatus) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderPrice = orderPrice;
        this.orderStatus = orderStatus;
    }

    public void updatePayed() {
        if (orderStatus == OrderStatus.PAYED) {
            throw new AlreadyPayedOrderException();
        }
        orderStatus = OrderStatus.PAYED;
    }
}
