package kr.co.koreazinc.spring.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.security.authentication.OAuthAuthenticationToken;

public class OAuthAuthenticationFilter extends OncePerRequestFilter {

    private final String DEFAULT_HEADER_NAME = "Authorization";

    private final String DEFAULT_TOKEN_TYPE = "Bearer";

    private List<RequestMatcher> requiresAuthenticationRequestMatchers;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private boolean ignoreFailure = true;

    private AuthenticationManager authenticationManager;

    public OAuthAuthenticationFilter(RequestMatcher ...requiresAuthenticationRequestMatchers) {
        this.requiresAuthenticationRequestMatchers = Arrays.asList(requiresAuthenticationRequestMatchers);
    }

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return this.requiresAuthenticationRequestMatchers.stream().anyMatch(matcher->matcher.matches(request));
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.ignoreFailure = false;
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    public String headerName;

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return Objects.<String>requireNonNullElse(this.headerName, DEFAULT_HEADER_NAME);
    }

    public String tokenType;

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenType() {
        return Objects.<String>requireNonNullElse(this.tokenType, DEFAULT_TOKEN_TYPE);
    }

    private String getOAuth(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader(this.getHeaderName());
        return StringUtils.trim(StringUtils.removeStart(authorization, this.getTokenType()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request, response)) {
            try {
                if (ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                    String oAuth = getOAuth(request, response);
                    if (StringUtils.isNotBlank(oAuth)) {
                        SecurityContextHolder.getContext().setAuthentication(getAuthenticationManager().authenticate(new OAuthAuthenticationToken(oAuth)));
                    } else {
                        throw new BadCredentialsException("Not Found Credentials (OAuth)");
                    }
                }
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                if (!ignoreFailure) {
                    this.getAuthenticationEntryPoint().commence(request, response, e);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}