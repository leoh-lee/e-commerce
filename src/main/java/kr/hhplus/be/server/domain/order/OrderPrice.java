package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;

@Embeddable
public class OrderPrice {

    @JoinColumn(nullable = false)
    private int basePrice;

    @JoinColumn(nullable = false)
    private int discountAmount;

    @JoinColumn(nullable = false)
    private int finalPrice;

}
