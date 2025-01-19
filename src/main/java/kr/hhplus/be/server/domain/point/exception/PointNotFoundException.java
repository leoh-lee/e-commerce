package kr.hhplus.be.server.domain.point.exception;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class PointNotFoundException extends ResourceNotFoundException {
    public PointNotFoundException() {
        super(ResponseCode.POINT_NOT_FOUND);
    }
}
