package kr.co.koreazinc.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class PassportAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;

    public PassportAuthenticationToken(String passport) {
        super(null);
        this.setDetails(null);
        this.credentials = passport;
    }

    public PassportAuthenticationToken(UserDetails userDetails, String passport) {
        super(userDetails.getAuthorities());
        this.setAuthenticated(true);
        this.setDetails(userDetails);
        this.credentials = passport;
    }

    @Override
    public UserDetails getPrincipal() {
        return (UserDetails) this.getDetails();
    }

    @Override
    public String getCredentials() {
        return this.credentials;
    }
}