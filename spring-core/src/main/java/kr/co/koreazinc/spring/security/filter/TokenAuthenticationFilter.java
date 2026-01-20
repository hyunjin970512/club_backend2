package kr.co.koreazinc.spring.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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
import kr.co.koreazinc.spring.security.authentication.TokenAuthenticationToken;
import kr.co.koreazinc.spring.security.functional.ClearToken;
import kr.co.koreazinc.spring.security.functional.GetToken;
import kr.co.koreazinc.spring.security.model.ResponseToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final List<RequestMatcher> requiresAuthenticationRequestMatchers;

    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private AuthenticationEntryPoint authenticationEntryPoint;

    private boolean ignoreFailure = true;
    private AuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(RequestMatcher... requiresAuthenticationRequestMatchers) {
        this.requiresAuthenticationRequestMatchers = Arrays.asList(requiresAuthenticationRequestMatchers);
    }

    private boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return this.requiresAuthenticationRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
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

    private GetToken getToken = (request, response) -> new ResponseToken();
    public void setGetToken(GetToken getToken) {
        this.getToken = getToken;
    }

    private ClearToken clearToken = (request, response) -> {};
    public void setClearToken(ClearToken clearToken) {
        this.clearToken = clearToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // ✅ 콜백/로그인은 제외 (원래 matcher가 /api/**라 사실 필요 없지만 안전)
        if ("/api/auth/login".equals(uri) || "/oauth/callback".equals(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean match = requiresAuthentication(request, response);
        if (match) {
            try {
                Authentication current = SecurityContextHolder.getContext().getAuthentication();
                String curType = (current == null) ? "null" : current.getClass().getName();
                Object curPrincipal = (current == null) ? null : current.getPrincipal();

                boolean needsAuth = (current == null) || (current instanceof AnonymousAuthenticationToken);

                log.info("[FILTER] uri={}, match=true, currentAuthType={}, currentPrincipalType={}, needsAuth={}",
                        uri,
                        curType,
                        (curPrincipal == null ? "null" : curPrincipal.getClass().getName()),
                        needsAuth
                );

                if (needsAuth) {
                    ResponseToken token = getToken.get(request, response);
                    String accessToken = (token == null) ? null : token.getAccessToken();
                    boolean tokenBlank = (accessToken == null || accessToken.isBlank() || "null".equalsIgnoreCase(accessToken));

                    log.info("[FILTER] uri={}, tokenBlank={}", uri, tokenBlank);

                    if (!tokenBlank) {
                        Authentication authenticated =
                                getAuthenticationManager().authenticate(new TokenAuthenticationToken(token));

                        SecurityContextHolder.getContext().setAuthentication(authenticated);

                        Object p = authenticated.getPrincipal();
                        log.info("[FILTER] authenticated ok. principalType={}, name={}",
                                (p == null ? "null" : p.getClass().getName()),
                                authenticated.getName()
                        );

                        if (ObjectUtils.isNotEmpty(getAuthenticationSuccessHandler())) {
                            this.getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authenticated);
                            if (response.isCommitted()) return;
                        }
                    }
                }
            } catch (AuthenticationException e) {
                log.warn("[FILTER] authentication failed. uri={}, msg={}", uri, e.getMessage());
                SecurityContextHolder.clearContext();
                clearToken.clear(request, response);

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
