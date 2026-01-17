package kr.co.koreazinc.spring.security.authentication;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import kr.co.koreazinc.spring.security.exception.UserJoinFailedException;
import kr.co.koreazinc.spring.security.model.ResponseToken;
import kr.co.koreazinc.spring.security.model.UserInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property;
import kr.co.koreazinc.spring.security.service.ExpandUserDetailsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {

    private final ExpandUserDetailsService userDetailsService;

    OAuth2Property property;

    public AuthorizationCodeAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = new ExpandUserDetailsService() {

            @Override
            public boolean isJoin() {
                return false;
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userDetailsService.loadUserByUsername(username);
            }

            @Override
            public UserDetails joinUser(UserInfo userDto) throws UserJoinFailedException {
                return null;
            }
        };
    }

    public AuthorizationCodeAuthenticationProvider(ExpandUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthorizationCodeAuthenticationToken.Credential credential = (AuthorizationCodeAuthenticationToken.Credential) authentication.getCredentials();
        try {
            ResponseToken responesDto = WebClient.create().post()
                .uri(credential.getTokenUrl())
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("client_id", credential.getClientId())
                    .with("redirect_uri", credential.getRedirectUri())
                    .with("code", credential.getCode()))
                .exchangeToMono(response->{
                    return response.bodyToMono(ResponseToken.class);
                }).block();
            if (ObjectUtils.isEmpty(responesDto) || responesDto.isBlank()) {
                log.warn("AuthorizationCodeAuthenticationProvider - authenticate: UserNotFoundException");
                throw new UsernameNotFoundException("Not Found User");
            }
            String accessToken = responesDto.getAccessToken();
            UserInfo userInfo = WebClient.create().get()
                .uri(credential.getUserInfoUrl())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .exchangeToMono(response->{
                    return response.bodyToMono(UserInfo.class);
                }).block();

            if (ObjectUtils.isEmpty(userInfo)) {
                log.warn("AuthorizationCodeAuthenticationProvider - authenticate: UserNotFoundException");
                throw new UsernameNotFoundException("Not Found User Info");
            }
            try {
                UserDetails userDetails = userDetailsService.loadUserByUserInfo(userInfo);
                if (ObjectUtils.isEmpty(userDetails)) {
                    userDetails = userDetailsService.loadUserByUsername(userInfo.getUserId());
                }
                return new AuthorizationCodeAuthenticationToken(isPossible(userDetails), credential);
            } catch (UsernameNotFoundException e) {
                if (!userDetailsService.isJoin()) {
                    throw e;
                }
                return new AuthorizationCodeAuthenticationToken(isPossible(userDetailsService.joinUser(userInfo)), credential);
            }
        } catch (RuntimeException e) {
            log.error("AuthorizationCodeAuthenticationProvider - authenticate: " + e.getMessage());
            throw new AuthenticationServiceException("Authentication Service Error (Authorization Code)");
        }
    }

    public UserDetails isPossible(UserDetails userDetails) {
        if (!userDetails.isAccountNonExpired()) {
            log.warn("AuthorizationCodeAuthenticationProvider - isPossible: AccountExpiredException");
            throw new AccountExpiredException("Account Expired User");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Credentials Expired User");
        }
        if (!userDetails.isEnabled()) {
            log.warn("AuthorizationCodeAuthenticationProvider - isPossible: DisabledException");
            throw new DisabledException("Disabled User");
        }
        return userDetails;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthorizationCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}