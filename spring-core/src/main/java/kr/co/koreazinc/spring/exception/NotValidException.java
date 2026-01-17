package kr.co.koreazinc.spring.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;

public class NotValidException extends RuntimeException {

    public NotValidException(MethodArgumentNotValidException exception) {
        super(exception.getBindingResult().getFieldError().getDefaultMessage());
    }

    public NotValidException(WebExchangeBindException exception) {
        super(exception.getBindingResult().getFieldError().getDefaultMessage());
    }
}