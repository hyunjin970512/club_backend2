package kr.co.koreazinc.spring.web.servlet.context;

import org.springframework.lang.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ContextResolver {

    String resolveContext(HttpServletRequest request);

    void setContext(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable String context);
}