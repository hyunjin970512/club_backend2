package kr.co.koreazinc.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class PasswordAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;

    public PasswordAuthenticationToken(String username, String password) {
        super(null);
        this.setDetails(username);
        this.credentials = password;
    }

    public PasswordAuthenticationToken(UserDetails userDetails, String password) {
        super(userDetails.getAuthorities());
        this.setAuthenticated(true);
        this.setDetails(userDetails);
        this.credentials = password;
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