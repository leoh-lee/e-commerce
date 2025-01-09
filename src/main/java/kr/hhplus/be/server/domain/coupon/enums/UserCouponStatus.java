package kr.hhplus.be.server.domain.coupon.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCouponStatus {
    ISSUED("발급"),
    USED("사용"),
    EXPIRED("만료"),
    CANCELLED("취소");

    private final String description;

    public boolean isIssued() {
        return this == ISSUED;
    }
}
