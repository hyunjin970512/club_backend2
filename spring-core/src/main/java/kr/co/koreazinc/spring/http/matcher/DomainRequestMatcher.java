package kr.co.koreazinc.spring.http.matcher;

import org.springframework.lang.NonNull;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.server.ServerWebExchange;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

public class DomainRequestMatcher implements RequestMatcher, ServerWebExchangeMatcher {

    private final String expression;

    public DomainRequestMatcher(@NonNull String expression) {
        this.expression = expression;
    }

    @Override
    public boolean matches(@NonNull HttpServletRequest request) {
        return matches(request.getServerName());
    }

    @Override
    public Mono<ServerWebExchangeMatcher.MatchResult> matches(@NonNull ServerWebExchange exchange) {
        if (matches(exchange.getRequest().getURI().getHost())) {
            return ServerWebExchangeMatcher.MatchResult.match();
        }
        return ServerWebExchangeMatcher.MatchResult.notMatch();
    }

    public boolean matches(String serverName) {
        if (this.expression == null) {
            return false;
        }
        return serverName.endsWith(this.expression);
    }
}