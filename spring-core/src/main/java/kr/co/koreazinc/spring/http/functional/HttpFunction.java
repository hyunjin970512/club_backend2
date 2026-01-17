package kr.co.koreazinc.spring.http.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

public interface HttpFunction {

    @FunctionalInterface
    public static interface Servlet<R> extends BiFunction<HttpServletRequest, HttpServletResponse, R> {  }

    @FunctionalInterface
    public static interface Reactive<R> extends Function<ServerWebExchange, Mono<R>> {  }
}