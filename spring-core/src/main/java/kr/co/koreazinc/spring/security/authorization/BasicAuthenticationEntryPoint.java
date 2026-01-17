package kr.co.koreazinc.spring.security.authorization;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

public class BasicAuthenticationEntryPoint {

    @Component
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class ServletEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            throw exception;
        }
    }

    @Component
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class ReactiveReactiveHandler implements ServerAuthenticationEntryPoint {

        @Override
        public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
            throw exception;
        }
    }
}