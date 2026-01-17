package kr.co.koreazinc.app.authentication;

import java.util.Map;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import kr.co.koreazinc.spring.security.authentication.TokenAuthenticationToken;
import kr.co.koreazinc.spring.security.model.ResponseToken;
import kr.co.koreazinc.spring.utility.JwtUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public TokenAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        ResponseToken token = (ResponseToken) authentication.getCredentials();
        String accessToken = token.getAccessToken().replace("Bearer ", "");
        if (JwtUtils.validationToken(accessToken, JwtUtils.getPublicKey())) {
            Map<String, Object> claims = JwtUtils.parseToken(accessToken, JwtUtils.getPublicKey());
            return new TokenAuthenticationToken(
                    userDetailsService.loadUserByUsername(
                            String.valueOf(claims.getOrDefault("userId", claims.get("accId")))),
                    token);
        }
        throw new BadCredentialsException("Invalid access token");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
