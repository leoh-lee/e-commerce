package kr.hhplus.be.server.infrastructures.external.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter // Properties 최초 세팅을 위해 Setter 필요
@Getter
@Component
@ConfigurationProperties("myapp.kafka.topics")
public class KafkaTopicsProperties {
    private OrderTopics order;

    @Getter
    @Setter
    public static class OrderTopics {
        private String created;
    }
}
