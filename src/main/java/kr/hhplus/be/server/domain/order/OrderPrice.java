package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderPrice {

    @JoinColumn(nullable = false)
    private int basePrice;

    @JoinColumn(nullable = false)
    private int discountAmount;

    @JoinColumn(nullable = false)
    private int finalPrice;

}
