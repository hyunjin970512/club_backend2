package kr.co.koreazinc.spring.security.authentication;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    public PasswordAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = String.valueOf(authentication.getDetails());
        String password = String.valueOf(authentication.getCredentials());
        try {
            return new PasswordAuthenticationToken(isPossible(userDetailsService.loadUserByUsername(username), password), password);
        } catch (RuntimeException e) {
            log.error("PasswordAuthenticationProvider - authenticate: " + e.getMessage());
            throw new AuthenticationServiceException("Authentication Service Error (Authorization Code)");
        }
    }

    public UserDetails isPossible(@NonNull UserDetails userDetails, String password) {
        if (!password.equals(userDetails.getPassword())) {
            log.warn("PasswordAuthenticationProvider - isPossible: BadCredentialsException");
            throw new BadCredentialsException("Bad Credentials (password)");
        }
        if (!userDetails.isAccountNonExpired()) {
            log.warn("PasswordAuthenticationProvider - isPossible: AccountExpiredException");
            throw new AccountExpiredException("Account Expired User");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Credentials Expired User");
        }
        if (!userDetails.isEnabled()) {
            log.warn("PasswordAuthenticationProvider - isPossible: DisabledException");
            throw new DisabledException("Disabled User");
        }
        return userDetails;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}