package kr.co.koreazinc.spring.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.security.authentication.AuthorizationCodeAuthenticationToken;
import kr.co.koreazinc.spring.security.property.OAuth2Property;

public class AuthorizationCodeAuthenticationFilter extends OncePerRequestFilter {

    private List<RequestMatcher> requiresAuthenticationRequestMatchers;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private boolean ignoreFailure = true;

    private AuthenticationManager authenticationManager;

    private OAuth2Property property;

    public AuthorizationCodeAuthenticationFilter(RequestMatcher ...requiresAuthenticationRequestMatchers) {
        this.requiresAuthenticationRequestMatchers = Arrays.asList(requiresAuthenticationRequestMatchers);
    }

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return this.requiresAuthenticationRequestMatchers.stream().anyMatch(matcher->matcher.matches(request));
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return this.authenticationSuccessHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return this.authenticationFailureHandler;
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

    public void setProperty(OAuth2Property property) {
        this.property = property;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request, response)) {
            try {
                if (ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                    String code = request.getParameter("code");
                    if (StringUtils.isNotBlank(code)) {
                        AuthorizationCodeAuthenticationToken.Credential credential = AuthorizationCodeAuthenticationToken.Credential.builder()
                            .code(code)
                            .clientId(property.getClient().getId())
                            .redirectUri(property.getClient().getRedirect().getLoginURL(request))
                            .tokenUrl(property.getProvider(OAuth2Property.Provider.AUTH).getTokenUrl())
                            .userInfoUrl(property.getProvider(OAuth2Property.Provider.AUTH).getUserInfoUrl())
                            .build();

                        SecurityContextHolder.getContext().setAuthentication(getAuthenticationManager().authenticate(new AuthorizationCodeAuthenticationToken(credential)));
                    } else {
                        throw new BadCredentialsException("Not Found Credentials (Authorization Code)");
                    }
                }
                if (ObjectUtils.isNotEmpty(getAuthenticationSuccessHandler())) {
                    this.getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, SecurityContextHolder.getContext().getAuthentication());
                    if (response.isCommitted()) return;
                }
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                if (ObjectUtils.isNotEmpty(getAuthenticationFailureHandler())) {
                    this.getAuthenticationFailureHandler().onAuthenticationFailure(request, response, e);
                    if (response.isCommitted()) return;
                }
                if (!ignoreFailure) {
                    this.getAuthenticationEntryPoint().commence(request, response, e);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}