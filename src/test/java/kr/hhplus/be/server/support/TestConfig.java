package kr.hhplus.be.server.support;

import kr.hhplus.be.server.infrastructures.external.dataplatform.DataPlatform;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public DataPlatform dataPlatform() {
        return new TestDataPlatform();
    }
}
