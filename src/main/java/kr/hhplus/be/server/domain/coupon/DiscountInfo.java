package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountInfo {

    @Min(100)
    private Integer discountAmount;

    @Min(1)
    @Max(100)
    private Integer discountRate;

}
