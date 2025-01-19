package kr.hhplus.be.server.domain.payment.exception;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class PaymentNotFoundException extends ResourceNotFoundException {

    public PaymentNotFoundException() {
        super(ResponseCode.PAYMENT_NOT_FOUND);
    }
}
