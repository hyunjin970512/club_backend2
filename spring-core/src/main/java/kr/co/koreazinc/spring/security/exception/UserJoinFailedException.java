package kr.co.koreazinc.spring.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UserJoinFailedException extends AuthenticationException {

    public UserJoinFailedException(String msg) {
        super(msg);
    }

    public UserJoinFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}