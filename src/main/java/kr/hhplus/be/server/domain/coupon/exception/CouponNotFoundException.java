package kr.hhplus.be.server.domain.coupon.exception;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class CouponNotFoundException extends ResourceNotFoundException {

    public CouponNotFoundException() {
        super(ResponseCode.COUPON_NOT_FOUND);
    }

}
