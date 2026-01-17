package kr.co.koreazinc.spring.filter;

import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class GlobalExceptionFilter {

    @Component
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class GlobalExceptionServletFilter extends OncePerRequestFilter
            implements Ordered {

        private HttpStatus resolveStatus(Exception exception) {
            if (exception instanceof AuthenticationException) {
                if (exception instanceof InsufficientAuthenticationException) {
                    return HttpStatus.UNAUTHORIZED;
                } else if (exception instanceof AccessDeniedException) {
                    return HttpStatus.FORBIDDEN;
                } else {
                    return HttpStatus.BAD_REQUEST;
                }
            } else {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                HttpEnhancer.create(request, response).forward(exception, resolveStatus(exception));
            }
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }

    @Component
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class GlobalExceptionReactiveFilter implements WebFilter, Ordered {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            return chain.filter(exchange).onErrorResume(exception -> {
                return HttpEnhancer.create(exchange).forward(exception,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            });
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}
