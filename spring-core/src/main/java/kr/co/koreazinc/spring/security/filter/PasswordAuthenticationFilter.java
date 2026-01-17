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
import kr.co.koreazinc.spring.security.authentication.PasswordAuthenticationToken;

public class PasswordAuthenticationFilter extends OncePerRequestFilter {

    private List<RequestMatcher> requiresAuthenticationRequestMatchers;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private boolean ignoreFailure = true;

    private AuthenticationManager authenticationManager;

    public PasswordAuthenticationFilter(RequestMatcher ...requiresAuthenticationRequestMatchers) {
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request, response)) {
            try {
                if (ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");

                    if (StringUtils.isNotBlank(username)) {
                        SecurityContextHolder.getContext().setAuthentication(getAuthenticationManager().authenticate(new PasswordAuthenticationToken(username, password)));
                        if (ObjectUtils.isNotEmpty(getAuthenticationSuccessHandler())) {
                            this.getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, SecurityContextHolder.getContext().getAuthentication());
                            if (response.isCommitted()) return;
                        }
                    } else {
                        throw new BadCredentialsException("Not Found Credentials (Username)");
                    }
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