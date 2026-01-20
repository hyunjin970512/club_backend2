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
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ResponseToken token = (ResponseToken) authentication.getCredentials();
        String accessToken = (token == null) ? null : token.getAccessToken();

        if (accessToken == null || accessToken.isBlank() || "null".equalsIgnoreCase(accessToken)) {
            throw new BadCredentialsException("Missing access token");
        }

        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        boolean valid = JwtUtils.validationToken(accessToken, JwtUtils.getPublicKey());
        log.info("[AUTH] jwt valid={}", valid);

        if (!valid) {
            throw new BadCredentialsException("Invalid access token");
        }

        Map<String, Object> claims = JwtUtils.parseToken(accessToken, JwtUtils.getPublicKey());
        log.info("[AUTH] claims={}", claims);

        String empNo = claims == null ? null : String.valueOf(claims.get("empNo"));
        if (empNo == null || empNo.isBlank() || "null".equalsIgnoreCase(empNo)) {
            throw new BadCredentialsException("Missing empNo claim");
        }

        var user = userDetailsService.loadUserByUsername(empNo);
        log.info("[AUTH] loaded user username={}", user.getUsername());

        return new TokenAuthenticationToken(user, token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
