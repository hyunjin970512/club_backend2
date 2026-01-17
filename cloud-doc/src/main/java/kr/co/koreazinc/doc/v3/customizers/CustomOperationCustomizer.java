package kr.co.koreazinc.doc.v3.customizers;

import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomOperationCustomizer implements GlobalOperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // TODO
        return operation;
    }
}