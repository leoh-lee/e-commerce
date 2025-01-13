package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderPrice {

    @JoinColumn(nullable = false)
    private BigDecimal basePrice;

    @JoinColumn(nullable = false)
    private BigDecimal discountAmount;

    @JoinColumn(nullable = false)
    private BigDecimal finalPrice;

}
