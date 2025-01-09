package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int productPrice;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductStock stock;

    public Product(String productName, int productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public void addStock(ProductStock stock) {
        this.stock = stock;
        stock.setProduct(this);
    }

    public void increaseStock(int amount) {
        stock.increase(amount);
    }

    public void decreaseStock(int amount) {
        stock.decrease(amount);
    }

}
