package kr.hhplus.be.server.support;

import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendRequest;
import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatformSendResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class TestDataPlatform implements DataPlatform {
    private final AtomicInteger sendCount = new AtomicInteger(0);

    public Integer getSentCount() {
        return sendCount.get();
    }

    @Override
    public DataPlatformSendResponse send(DataPlatformSendRequest data) {
        sendCount.getAndIncrement();
        return null;
    }
}
