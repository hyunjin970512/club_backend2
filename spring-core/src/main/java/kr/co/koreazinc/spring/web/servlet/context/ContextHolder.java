package kr.co.koreazinc.spring.web.servlet.context;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class ContextHolder {

    public static final String CONTEXT_RESOLVER_ATTRIBUTE = ContextHolder.class.getName() + ".CONTEXT_RESOLVER";

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }

    private static ContextResolver getContextResolver() {
        return (ContextResolver) ContextHolder.getRequest().getAttribute(CONTEXT_RESOLVER_ATTRIBUTE);
    }

    public static String getContext() {
        return ContextHolder.getContextResolver().resolveContext(ContextHolder.getRequest());
    }
}