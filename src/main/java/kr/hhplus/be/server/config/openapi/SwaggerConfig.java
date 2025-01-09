package kr.hhplus.be.server.config.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // swagger default response 제거
    @Bean
    public OpenApiCustomizer removeDefaultResponse() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperations().forEach(operation ->
                        operation.getResponses().remove("default")
                )
        );
    }

}
