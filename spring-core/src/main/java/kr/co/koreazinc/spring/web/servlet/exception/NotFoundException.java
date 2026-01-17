package kr.co.koreazinc.spring.web.servlet.exception;

import jakarta.servlet.ServletException;

public class NotFoundException extends ServletException {

    public NotFoundException(String message) {
        super(message);
    }
}