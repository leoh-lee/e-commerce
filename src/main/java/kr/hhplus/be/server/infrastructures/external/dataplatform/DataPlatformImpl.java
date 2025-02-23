package kr.hhplus.be.server.infrastructures.external.dataplatform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformImpl implements DataPlatform {
    @Override
    public DataPlatformSendResponse send(DataPlatformSendRequest data) {
        log.info("Send to DataPlatform. Request Type is {}", data.requestType());
        return new DataPlatformSendResponse();
    }
}
