package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.point.exception.PointLimitExceededException;
import kr.hhplus.be.server.domain.point.exception.PointNotEnoughException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Point extends BaseEntity {

    private static final int MAX_BALANCE = 2_000_000;
    private static final int BALANCE_UNIT = 2_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Max(value = 2_000_000)
    @Column(nullable = false)
    private int balance;

    public Point(User user, int balance) {
        this.user = user;
        this.balance = balance;
    }

    public void usePoint(int amount) {
        if (balance < amount) {
            throw new PointNotEnoughException();
        }

        this.balance -= amount;
    }

    public void chargePoint(int amount) {
        int chargedPoint = balance + amount;

        if (chargedPoint > MAX_BALANCE) {
            throw new PointLimitExceededException();
        }

        this.balance = chargedPoint;
    }
}
