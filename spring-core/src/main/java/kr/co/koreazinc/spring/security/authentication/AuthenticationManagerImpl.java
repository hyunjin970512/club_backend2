package kr.co.koreazinc.spring.security.authentication;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AuthenticationManagerImpl implements AuthenticationManager {

    private final List<AuthenticationProvider> authenticationProviders = new ArrayList<AuthenticationProvider>();

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProviders.add(authenticationProvider);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        for (AuthenticationProvider authenticationProvider : authenticationProviders) {
            if (authenticationProvider.supports(authentication.getClass())) {
                authentication = authenticationProvider.authenticate(authentication);
                if (ObjectUtils.isNotEmpty(authentication.getDetails())) {
                    return authentication;
                }
            }
        }
        throw new AuthenticationServiceException("No provider supports authentication");
    }
}