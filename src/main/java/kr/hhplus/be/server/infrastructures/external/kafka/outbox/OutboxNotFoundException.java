package kr.hhplus.be.server.infrastructures.external.kafka.outbox;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.support.http.response.ResponseCode;

public class OutboxNotFoundException extends ResourceNotFoundException {
    protected OutboxNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
