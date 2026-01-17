package kr.co.koreazinc.spring.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Getter;

public class AuthorizationCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Credential credentials;

    public AuthorizationCodeAuthenticationToken(Credential code) {
        super(null);
        this.setDetails(null);
        this.credentials = code;
    }

    public AuthorizationCodeAuthenticationToken(UserDetails userDetails, Credential code) {
        super(userDetails.getAuthorities());
        this.setAuthenticated(true);
        this.setDetails(userDetails);
        this.credentials = code;
    }

    @Override
    public UserDetails getPrincipal() {
        return (UserDetails) this.getDetails();
    }

    @Override
    public Credential getCredentials() {
        return this.credentials;
    }

    @Getter
    @Builder
    public static class Credential {

        private String code;

        private String clientId;

        private String redirectUri;

        private String tokenUrl;

        private String userInfoUrl;
    }
}