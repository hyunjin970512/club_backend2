package kr.co.koreazinc.spring.security.authentication;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.koreazinc.spring.security.property.OAuth2Property.Credential;
import kr.co.koreazinc.spring.utility.OAuthUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuthAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private Credential credential;

    public OAuthAuthenticationProvider(UserDetailsService userDetailsService, Credential credential) {
        this.userDetailsService = userDetailsService;
        this.credential = credential;
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String oAuth = String.valueOf(authentication.getCredentials());
        if (!OAuthUtils.validationToken(oAuth, credential.getDiscoveryUrl())) throw new BadCredentialsException("Bad Credentials (OAuth)");

        Map<String, Object> claims = OAuthUtils.parseToken(oAuth, credential.getDiscoveryUrl(), credential.getClientUrl());
        String clientId = String.valueOf(claims.get("appid")).replace("\"", "");
        UserDetails userDetails = userDetailsService.loadUserByUsername(clientId);
        if (ObjectUtils.isEmpty(userDetails)) {
            log.warn("OAuthAuthenticationProvider - authenticate: UserNotFoundException");
            throw new UsernameNotFoundException("Not Found User");
        }
        try {
            List<String> roles = mapper.readValue(String.valueOf(claims.get("roles")), new TypeReference<List<String>>() {});
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            if (authorities instanceof List) {
                try {
                    List<SimpleGrantedAuthority> modifiable = (List<SimpleGrantedAuthority>) authorities;
                    for (String role : roles) {
                        modifiable.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    }
                } catch (UnsupportedOperationException e) {
                    log.warn("OAuthAuthenticationProvider - authenticate" + e.getMessage());
                }
            } else {
                log.warn("OAuthAuthenticationProvider - authenticate is not List" + authorities.getClass().getName());
            }
        } catch (IOException e) {
            log.error("OAuthAuthenticationProvider - authenticate: " + e.getMessage());
            throw new AuthenticationServiceException("Authentication Service Error (OAuth)");
        }
        return new OAuthAuthenticationToken(userDetails, oAuth);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuthAuthenticationToken.class.isAssignableFrom(authentication);
    }
}