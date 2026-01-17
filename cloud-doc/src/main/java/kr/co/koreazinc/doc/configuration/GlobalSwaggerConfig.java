package kr.co.koreazinc.doc.configuration;

import static org.springdoc.core.utils.Constants.SPRINGDOC_SWAGGER_UI_ENABLED;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SPRINGDOC_SWAGGER_UI_ENABLED, matchIfMissing = true)
@RequiredArgsConstructor
public class GlobalSwaggerConfig {

    private final SwaggerUiConfigProperties swaggerConfigProperties;

    @PostConstruct
    public void init() {
        log.info(String.format("Swagger-UI endpoints beneath base path '%s'", swaggerConfigProperties.getPath()));
    }
}