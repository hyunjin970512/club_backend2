package kr.co.koreazinc.app.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi v1() {
        return GroupedOpenApi.builder().group("V1").pathsToMatch("/v1/**").build();
    }
}
