package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "order_product", indexes = @Index(name = "idx_product_quantity", columnList = "created_at, product_id, quantity"))
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long productId;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    public OrderProduct(Long orderId, Long productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

}
