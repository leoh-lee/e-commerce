package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.point.exception.PointLimitExceededException;
import kr.hhplus.be.server.domain.point.exception.PointNotEnoughException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Point extends BaseEntity {

    private static final BigDecimal MAX_BALANCE2 = BigDecimal.valueOf(2_000_000);
    private static final BigDecimal BALANCE_UNIT2 = BigDecimal.valueOf(2_000_000);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Max(value = 2_000_000)
    @Column(nullable = false)
    private BigDecimal balance;

    public Point(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public void usePoint(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new PointNotEnoughException();
        }

        this.balance = this.balance.subtract(amount);
    }

    public void chargePoint(BigDecimal amount) {
        BigDecimal chargedPoint = balance.add(amount);

        if (chargedPoint.compareTo(MAX_BALANCE2) > 0) {
            throw new PointLimitExceededException();
        }

        this.balance = chargedPoint;
    }
}
