package kr.co.koreazinc.spring.security.functional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface ClearToken {

    void clear(HttpServletRequest request, HttpServletResponse response);
}