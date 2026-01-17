package kr.co.koreazinc.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class OAuthAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;

    public OAuthAuthenticationToken(String oAuth) {
        super(null);
        this.setDetails(null);
        this.credentials = oAuth;
    }

    public OAuthAuthenticationToken(UserDetails userDetails, String oAuth) {
        super(userDetails.getAuthorities());
        this.setAuthenticated(true);
        this.setDetails(userDetails);
        this.credentials = oAuth;
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