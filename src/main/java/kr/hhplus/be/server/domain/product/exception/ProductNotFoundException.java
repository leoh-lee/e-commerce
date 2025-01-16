package kr.hhplus.be.server.domain.product.exception;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException() {
        super(ResponseCode.PRODUCT_NOT_FOUND);
    }
}
