package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType transactionType;

    @Column(nullable = false)
    private int amount;

    public PointHistory(Point point, PointTransactionType transactionType, int amount) {
        this.point = point;
        this.transactionType = transactionType;
        this.amount = amount;
    }
}
