package kr.co.koreazinc.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
import kr.co.koreazinc.spring.security.utility.AuthenticationTokenUtils;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig {

    // ✅ 표준앱 쿠키명 (메인)
    private static final String ACCESS_TOKEN_COOKIE = "ACCESS-TOKEN";
    // ✅ 레거시/구버전 쿠키명 (청소용)
    private static final String ACCESS_TOKEN_COOKIE_LEGACY = "ACCESS_TOKEN";

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
                    "/oauth/callback",
                    "/oauth/refresh",          // refresh는 인증없이 호출 가능하게(401 대응용)
                    "/api/common/doc/download/**",
                    "/api/club/join/check/**",
                    "/api/inbox/stream",
                    "/api/push/app/**",

                    "/api/clubs/**",
                    "/api/together/**"
                ).permitAll()

                .requestMatchers("/api/inbox/**").authenticated()

                .requestMatchers(
                    "/api/**",
                    "/main/**",
                    "/api/club/**"
                ).access(accessChecker)

                .anyRequest().permitAll()
            );

            // ✅ /api/** 에만 토큰 인증 필터 적용
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
        TokenAuthenticationFilter filter =
            new TokenAuthenticationFilter(new AntPathRequestMatcher("/api/**"));

        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationEntryPoint(authenticationEntryPoint);

        /**
         * 표준앱 방식으로 통일:
         * - Authorization 헤더(Bearer) 우선
         * - 없으면 Cookie(ACCESS-TOKEN)
         * (유틸에 이미 구현돼있음)
         */
        filter.setGetToken((request, response) -> {
            String token = AuthenticationTokenUtils.getAccessToken(request);

            // 혹시 예전 쿠키 ACCESS_TOKEN만 남아있으면 fallback
            if ((token == null || token.isBlank()) && request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie c : request.getCookies()) {
                    if (ACCESS_TOKEN_COOKIE_LEGACY.equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }

            System.out.println("[GETTOKEN] uri=" + request.getRequestURI()
                + ", tokenPresent=" + (token != null && !token.isBlank()));

            return ResponseToken.builder().accessToken(token).build();
        });

        // ✅ 실패 시 쿠키 정리 (둘 다 삭제)
        filter.setClearToken((request, response) -> {
            HttpEnhancer.create(request, response).delCookie(ACCESS_TOKEN_COOKIE);
            HttpEnhancer.create(request, response).delCookie(ACCESS_TOKEN_COOKIE_LEGACY);
        });

        return filter;
    }
}
