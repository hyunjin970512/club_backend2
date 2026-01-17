package kr.co.koreazinc.spring.http.matcher;

import java.net.URI;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import reactor.core.publisher.Mono;

public class PrefixPathRequestMatcher implements RequestMatcher, ServerWebExchangeMatcher {

    private final String[] prefix;

    public PrefixPathRequestMatcher(String ...prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean matches(@NonNull HttpServletRequest request) {
        return matches(HttpEnhancer.create(request).getURI());
    }

    @Override
    public Mono<ServerWebExchangeMatcher.MatchResult> matches(@NonNull ServerWebExchange exchange) {
        if (matches(exchange.getRequest().getURI())) {
            return ServerWebExchangeMatcher.MatchResult.match();
        }
        return ServerWebExchangeMatcher.MatchResult.notMatch();
    }

    public boolean matches(@NonNull URI uri) {
        return Stream.of(prefix).anyMatch(p->uri.getPath().startsWith(p));
    }

    public boolean matches(@NonNull String path) {
        return Stream.of(prefix).anyMatch(p->path.startsWith(p));
    }
}