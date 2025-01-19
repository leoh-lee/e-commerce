package kr.hhplus.be.server.domain.order.exception;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class OrderNotFoundException extends ResourceNotFoundException {

    public OrderNotFoundException() {
        super(ResponseCode.ORDER_NOT_FOUND);
    }

}
