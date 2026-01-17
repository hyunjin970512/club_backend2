package kr.co.koreazinc.spring.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.web.servlet.context.ContextHolder;
import kr.co.koreazinc.spring.web.servlet.context.ContextResolver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class AttributeContextFilter {

    public static final String CONTEXT_KEY = AttributeContextFilter.class.getName() +  ".context.key";

    @Component
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class AttributeContextServletFilter extends OncePerRequestFilter implements Ordered {

        @Autowired(required = false)
        private ContextResolver contextResolver;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            // ContextResolver 등록
            request.setAttribute(ContextHolder.CONTEXT_RESOLVER_ATTRIBUTE, contextResolver);

            filterChain.doFilter(request, response);
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }

    @Component
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class AttributeContextReactiveFilter implements WebFilter, Ordered {

        public  final Map<String, Map<String, Object>> contextHolder = new ConcurrentHashMap<>();

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            // Context(Attribute) 등록
            if (exchange.getRequest().getHeaders().containsKey(AttributeContextFilter.CONTEXT_KEY)) {
                String contextKey = exchange.getRequest().getHeaders().getFirst(AttributeContextFilter.CONTEXT_KEY);
                if (contextHolder.containsKey(contextKey)) {
                    Map<String, Object> context = contextHolder.get(contextKey);
                    for (String key : context.keySet()) {
                        if (!exchange.getRequest().getAttributes().containsKey(key)) {
                            exchange.getRequest().getAttributes().put(key, context.get(key));
                        }
                    }
                }
            }
            // Context 저장
            contextHolder.put(exchange.getRequest().getId(), exchange.getRequest().getAttributes());
            return chain.filter(exchange).doFinally(signalType->{
                contextHolder.remove(exchange.getRequest().getId());
            });
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}