package kr.co.koreazinc.spring.security.functional;

import java.util.NoSuchElementException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.security.model.ResponseToken;

@FunctionalInterface
public interface GetToken {

    ResponseToken get(HttpServletRequest request, HttpServletResponse response) throws NoSuchElementException;
}