package kr.co.koreazinc.doc.v3.customizers;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomGlobalOpenApiCustomizer implements GlobalOpenApiCustomizer {

    @Override
    public void customise(OpenAPI openApi) {
        // TODO:
    }
}