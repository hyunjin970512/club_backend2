package kr.co.koreazinc.spring.web.reactive;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public interface HandlerInterceptor extends WebFilter, Ordered {

    public default Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (preHandle(exchange)) {
            return chain.filter(exchange).doOnSuccess((aVoid)->{
                postHandle(exchange);
            }).doOnError((throwable)->{
                afterCompletion(exchange, throwable);
            });
        }
        return Mono.empty();
    }

    public default boolean preHandle(ServerWebExchange exchange) {
        return true;
    }

    public default void postHandle(ServerWebExchange exchange) {

    }

    public default void afterCompletion(ServerWebExchange exchange, @Nullable Throwable throwable) {

    }

    @Override
    public default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}