package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponUsableDate {
    @Column(nullable = false)
    private LocalDateTime usableStartDt;

    @Column(nullable = false)
    private LocalDateTime usableEndDt;

    public boolean isUsable(LocalDateTime now) {
        return usableStartDt.isBefore(now) && usableEndDt.isAfter(now);
    }
}
