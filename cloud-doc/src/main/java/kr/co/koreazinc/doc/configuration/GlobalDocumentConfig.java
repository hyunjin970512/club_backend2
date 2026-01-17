package kr.co.koreazinc.doc.configuration;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
@RequiredArgsConstructor
public class GlobalDocumentConfig {

    private final SpringDocConfigProperties documentConfigProperties;

    @PostConstruct
    public void init() {
        log.info(String.format("API-DOCS endpoints beneath base path '%s'", documentConfigProperties.getApiDocs().getPath()));
    }
}