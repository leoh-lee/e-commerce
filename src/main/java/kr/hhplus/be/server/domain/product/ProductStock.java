package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.exception.StockNotEnoughException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int stock;

    public ProductStock(Product product, int stock) {
        this.product = product;
        this.stock = stock;
    }

    public void increase(int amount) {
        stock += amount;
    }

    public void decrease(int amount) {
        if (stock < amount) {
            throw new StockNotEnoughException();
        }

        stock -= amount;
    }

}
