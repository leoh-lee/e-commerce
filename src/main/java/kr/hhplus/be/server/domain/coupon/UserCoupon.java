package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.coupon.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotUsableException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_coupon",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "couponId"})
        }
)
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus userCouponStatus;

    private LocalDateTime expiredDate;

    private LocalDateTime useDate;

    public UserCoupon(Long userId, Long couponId, UserCouponStatus userCouponStatus, LocalDateTime expiredDate, LocalDateTime useDate) {
        this.userId = userId;
        this.couponId = couponId;
        this.userCouponStatus = userCouponStatus;
        this.expiredDate = expiredDate;
        this.useDate = useDate;
    }

    public boolean isUsable(LocalDateTime now) {
        return expiredDate.isAfter(now) && userCouponStatus.isIssued();
    }

    public void changeUseStatus(LocalDateTime now) {
        if (!userCouponStatus.isIssued() || expiredDate.isAfter(now)) {
            throw new CouponNotUsableException();
        }

        userCouponStatus = UserCouponStatus.USED;
        useDate = now;
    }

}
