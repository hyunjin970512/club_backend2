package kr.co.koreazinc.spring.filter;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.http.utility.UriUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class RequestDecoratingFilter {

    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    @Component
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class GlobalExceptionServletFilter extends OncePerRequestFilter implements Ordered {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String scheme = Optional.ofNullable(HttpEnhancer.create(request).getHeaders().getFirst(X_FORWARDED_PROTO)).orElse(request.getScheme());
            filterChain.doFilter(new HttpServletRequestWrapper(request) {

                @Override
                public String getScheme() {
                    return scheme;
                }

                @Override
                public StringBuffer getRequestURL() {
                    // X-Forwarded-Proto 헤더가 존재하면 해당 프로토콜로 변경
                    return new StringBuffer(UriUtils.mutate(URI.create(super.getRequestURL().toString())).scheme(scheme).build().toString());
                }
            }, response);
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
            String scheme = Optional.ofNullable(HttpEnhancer.create(exchange).getHeaders().getFirst(RequestDecoratingFilter.X_FORWARDED_PROTO)).orElse(exchange.getRequest().getURI().getScheme());
            return chain.filter(exchange.mutate().request(
                new ServerHttpRequestDecorator(exchange.getRequest()) {

                    @Override
                    public URI getURI() {
                        // X-Forwarded-Proto 헤더가 존재하면 해당 프로토콜로 변경
                        return UriUtils.mutate(super.getURI()).scheme(scheme).build();
                    }
                }).build()
            );
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}