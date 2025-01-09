package kr.hhplus.be.server.infrastructures.external.dataplatform;

public interface DataPlatform {

    DataPlatformSendResponse send(DataPlatformSendRequest data);

}
