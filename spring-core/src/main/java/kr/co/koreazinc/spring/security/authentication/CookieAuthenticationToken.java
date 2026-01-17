package kr.co.koreazinc.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.Cookie;

public class CookieAuthenticationToken extends AbstractAuthenticationToken {

    private final Cookie credentials;

    public CookieAuthenticationToken(Cookie token) {
        super(null);
        this.setDetails(null);
        this.credentials = token;
    }

    public CookieAuthenticationToken(UserDetails userDetails, Cookie cookie) {
        super(userDetails.getAuthorities());
        this.setAuthenticated(true);
        this.setDetails(userDetails);
        this.credentials = cookie;
    }

    @Override
    public UserDetails getPrincipal() {
        return (UserDetails) this.getDetails();
    }

    @Override
    public Cookie getCredentials() {
        return this.credentials;
    }
}