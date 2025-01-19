package kr.hhplus.be.server.domain.user.exception;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException() {
        super(ResponseCode.USER_NOT_FOUND);
    }

}
