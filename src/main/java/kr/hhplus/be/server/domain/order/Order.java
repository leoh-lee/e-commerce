package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
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
            return;
        }

        orderStatus = OrderStatus.PAYED;
    }
}
