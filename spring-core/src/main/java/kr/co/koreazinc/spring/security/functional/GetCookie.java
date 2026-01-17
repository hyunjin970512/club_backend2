package kr.co.koreazinc.spring.security.functional;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface GetCookie {

    Optional<Cookie> get(HttpServletRequest request, HttpServletResponse response);
}