package kr.co.koreazinc.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.Cookie;
import kr.co.koreazinc.app.authentication.AuthorizationAccessChecker;
import kr.co.koreazinc.app.authentication.TokenAuthenticationProvider;
import kr.co.koreazinc.app.service.security.UserDetailsServiceImpl;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.security.adapter.CoreSecurityConfigurerAdapter;
import kr.co.koreazinc.spring.security.authentication.AuthenticationManagerImpl;
import kr.co.koreazinc.spring.security.authorization.BasicAccessDeniedHandler;
import kr.co.koreazinc.spring.security.authorization.BasicAuthenticationEntryPoint;
import kr.co.koreazinc.spring.security.filter.TokenAuthenticationFilter;
import kr.co.koreazinc.spring.security.model.ResponseToken;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    // ✅ 쿠키명 통일
    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    // ✅ 레거시 쿠키명(청소용)
    private static final String ACCESS_TOKEN_COOKIE_LEGACY = "ACCESS-TOKEN";

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthorizationAccessChecker accessChecker;
    private final BasicAccessDeniedHandler.ServletHandler accessDeniedHandler;
    private final BasicAuthenticationEntryPoint.ServletEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return CoreSecurityConfigurerAdapter.init(http, (security) -> {

            security.authorizeHttpRequests(request -> CoreSecurityConfigurerAdapter.Request
                .init(request)
                .requestMatchers(
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/api/auth/login",
                    "/api/clubs/**",
                    "/oauth/callback"
                ).permitAll()
                .requestMatchers(
                		"/api/**",
                		"/main/**"
                		).access(accessChecker)
                .anyRequest().permitAll()
            );

            // ✅ API 요청에만 토큰 인증 필터 적용
            security.addFilterAt(tokenAuthenticationFilter(), BasicAuthenticationFilter.class);

            security.exceptionHandling(handler -> handler
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            );

            return security;
        });
    }

    @Bean
    public AuthenticationManagerImpl authenticationManager() {
        AuthenticationManagerImpl authenticationManager = new AuthenticationManagerImpl();
        authenticationManager.setAuthenticationProvider(new TokenAuthenticationProvider(userDetailsService));
        return authenticationManager;
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        // ✅ /api/** 에만 적용
        TokenAuthenticationFilter filter =
            new TokenAuthenticationFilter(new AntPathRequestMatcher("/api/**"));

        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationEntryPoint(authenticationEntryPoint);

        // ✅ 토큰 읽기: Authorization 헤더 → Cookie(ACCESS_TOKEN) → 레거시(ACCESS-TOKEN)
        filter.setGetToken((request, response) -> {
            String token = null;

            // 1) Authorization: Bearer xxx
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                token = auth.substring(7);
            }

            // 2) Cookie ACCESS_TOKEN
            if ((token == null || token.isBlank()) && request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if (ACCESS_TOKEN_COOKIE.equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }

            // 3) Legacy cookie ACCESS-TOKEN
            if ((token == null || token.isBlank()) && request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if (ACCESS_TOKEN_COOKIE_LEGACY.equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }

            // 너무 길게 찍지 말고 존재 여부만
            System.out.println("[GETTOKEN] uri=" + request.getRequestURI() + ", tokenPresent=" + (token != null && !token.isBlank()));

            return ResponseToken.builder().accessToken(token).build();
        });

        // ✅ 실패/로그아웃 시 쿠키 정리 (두 개 다 삭제)
        filter.setClearToken((request, response) -> {
            HttpEnhancer.create(request, response).delCookie(ACCESS_TOKEN_COOKIE);
            HttpEnhancer.create(request, response).delCookie(ACCESS_TOKEN_COOKIE_LEGACY);
        });


        return filter;
    }
}
