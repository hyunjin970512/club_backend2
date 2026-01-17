package kr.co.koreazinc.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import kr.co.koreazinc.spring.security.model.ResponseToken;

public class TokenAuthenticationToken extends AbstractAuthenticationToken {

    private final ResponseToken credentials;

    public TokenAuthenticationToken(ResponseToken token) {
        super(null);
        this.setDetails(null);
        this.credentials = token;
    }

    public TokenAuthenticationToken(UserDetails userDetails, ResponseToken token) {
        super(userDetails.getAuthorities());
        this.setAuthenticated(true);
        this.setDetails(userDetails);
        this.credentials = token;
    }

    @Override
    public UserDetails getPrincipal() {
        return (UserDetails) this.getDetails();
    }

    @Override
    public ResponseToken getCredentials() {
        return this.credentials;
    }
}