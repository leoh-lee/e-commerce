package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String couponName;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private int discountAmount;

    private int discountRate;

    @Column(nullable = false)
    private LocalDateTime usableStartDt;

    @Column(nullable = false)
    private LocalDateTime usableEndDt;

    @Column(nullable = false)
    private int couponStock;

}
