package kr.co.koreazinc.spring.exception;

public class TokenIssuanceException extends RuntimeException {

    public TokenIssuanceException() {
        super("Failed to issue token");
    }

    public TokenIssuanceException(String msg) {
        super(msg);
    }

    public TokenIssuanceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}