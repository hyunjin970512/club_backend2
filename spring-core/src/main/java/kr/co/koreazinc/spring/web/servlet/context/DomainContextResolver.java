package kr.co.koreazinc.spring.web.servlet.context;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DomainContextResolver implements ContextResolver {

    public static final String CONTEXT_REQUEST_ATTRIBUTE_NAME = DomainContextResolver.class.getName() + ".CONTEXT";

    private final Set<DomainContext> domainContexts;

    public DomainContextResolver(DomainContextMatcher matcher) {
        if (matcher == null) {
            log.info("DomainContextMatcher is null. No domain contexts are configured.");
            this.domainContexts = Set.of();
        } else {
            this.domainContexts = matcher.getDomainContexts();
        }
    }

    public DomainContextResolver() {
        this(Set::of);
    }

    @Override
    public String resolveContext(HttpServletRequest request) {
        parseContextDomainIfNecessary(request);
        return (String) request.getAttribute(CONTEXT_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public void setContext(HttpServletRequest request, HttpServletResponse response, String context) {
        request.setAttribute(CONTEXT_REQUEST_ATTRIBUTE_NAME, context);
    }

    private void parseContextDomainIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(CONTEXT_REQUEST_ATTRIBUTE_NAME) == null) {
            for (DomainContext domainContext : domainContexts) {
                if (domainContext.getMatcher().matches(request)) {
                    request.setAttribute(CONTEXT_REQUEST_ATTRIBUTE_NAME, domainContext.getContext());
                }
            }
        }
    }
}