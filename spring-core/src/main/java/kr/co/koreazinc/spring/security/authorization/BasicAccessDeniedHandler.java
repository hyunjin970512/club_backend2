package kr.co.koreazinc.spring.security.authorization;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

public class BasicAccessDeniedHandler {

    @Component
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class ServletHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
            throw exception;
        }
    }

    @Component
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class ReactiveHandler implements ServerAccessDeniedHandler {

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException exception) {
            throw exception;
        }
    }
}