package kr.co.koreazinc.spring.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.security.authentication.CookieAuthenticationToken;
import kr.co.koreazinc.spring.security.functional.GetCookie;
import kr.co.koreazinc.spring.security.utility.AuthenticationTokenUtils;

public class CookieAuthenticationFilter extends OncePerRequestFilter {

    private List<RequestMatcher> requiresAuthenticationRequestMatchers;

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler authenticationFailureHandler;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private boolean ignoreFailure = true;

    private AuthenticationManager authenticationManager;

    public CookieAuthenticationFilter(RequestMatcher ...requiresAuthenticationRequestMatchers) {
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

    private GetCookie getCookie = (request, response) -> {
        return HttpEnhancer.create(request).getCookie(AuthenticationTokenUtils.LOGIN_HINT_COOKIE_NAME);
    };

    public void setGetToken(GetCookie getCookie) {
        this.getCookie = getCookie;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request, response)) {
            try {
                if (ObjectUtils.isEmpty(SecurityContextHolder.getContext().getAuthentication())) {
                    Optional<Cookie> cookie = getCookie.get(request, response);
                    if (cookie.isPresent()) {
                        SecurityContextHolder.getContext().setAuthentication(getAuthenticationManager().authenticate(new CookieAuthenticationToken(cookie.get())));
                        if (ObjectUtils.isNotEmpty(getAuthenticationSuccessHandler())) {
                            this.getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, SecurityContextHolder.getContext().getAuthentication());
                            if (response.isCommitted()) return;
                        }
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