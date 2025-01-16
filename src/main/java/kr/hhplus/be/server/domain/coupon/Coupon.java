package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CouponInfo couponInfo;

    @Embedded
    private DiscountInfo discountInfo;

    @Embedded
    private CouponUsableDate couponUsableDate;

    public Coupon(CouponInfo couponInfo, DiscountInfo discountInfo, CouponUsableDate couponUsableDate) {
        this.couponInfo = couponInfo;
        this.discountInfo = discountInfo;
        this.couponUsableDate = couponUsableDate;
    }

    public boolean isUsable(LocalDateTime now) {
        return couponUsableDate.isUsable(now);
    }

    public void decreaseStock() {
        couponInfo.decreaseStock();
    }

    public BigDecimal getDiscountPrice(BigDecimal price) {
        if (couponInfo.isPercentageType()) {
            Integer discountRate = discountInfo.getDiscountRate();
            return price.multiply(BigDecimal.valueOf(discountRate)).divide(BigDecimal.valueOf(100));
        }

        Integer discountAmount = discountInfo.getDiscountAmount();
        if (BigDecimal.valueOf(discountAmount).compareTo(price) > 0) {
            return price;
        }
        return BigDecimal.valueOf(discountAmount);
    }

}
