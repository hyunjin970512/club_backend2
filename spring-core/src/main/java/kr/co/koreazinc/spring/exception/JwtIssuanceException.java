package kr.co.koreazinc.spring.exception;

public class JwtIssuanceException extends RuntimeException {

    public JwtIssuanceException() {
        super("Failed to issue JWT");
    }

    public JwtIssuanceException(String msg) {
        super(msg);
    }

    public JwtIssuanceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}