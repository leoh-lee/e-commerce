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
    @GeneratedValue
    private Long id;

    private Long userId;

    private Long userCouponId;

    @Embedded
    private OrderPrice orderPrice;

    public Order(Long userId, Long userCouponId, OrderPrice orderPrice) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderPrice = orderPrice;
    }
}
