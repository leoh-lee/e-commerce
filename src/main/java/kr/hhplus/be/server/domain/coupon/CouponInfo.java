package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.domain.coupon.enums.CouponType;
import kr.hhplus.be.server.domain.coupon.exception.CouponStockNotEnoughException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponInfo {

    @Column(nullable = false)
    private String couponName;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @Column(nullable = false)
    private int couponStock;

    public void decreaseStock() {
        if (couponStock <= 0) {
            throw new CouponStockNotEnoughException();
        }
        couponStock--;
    }

    public boolean isPercentageType() {
        return couponType == CouponType.PERCENTAGE;
    }

}
