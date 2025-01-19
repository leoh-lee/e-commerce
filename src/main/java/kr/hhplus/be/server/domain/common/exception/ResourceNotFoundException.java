package kr.hhplus.be.server.domain.common.exception;

import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.Getter;

@Getter
public abstract class ResourceNotFoundException extends RuntimeException {
    protected final ResponseCode responseCode;

    protected ResourceNotFoundException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

}
