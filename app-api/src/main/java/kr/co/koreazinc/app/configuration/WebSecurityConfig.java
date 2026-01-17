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

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthorizationAccessChecker accessChecker;

    private final BasicAccessDeniedHandler.ServletHandler accessDeniedHandler;

    private final BasicAuthenticationEntryPoint.ServletEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return CoreSecurityConfigurerAdapter.init(http, (security) -> {
            security.authorizeHttpRequests(request -> CoreSecurityConfigurerAdapter.Request
                    .init(request).requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                    .anyRequest().access(accessChecker));

            security.addFilterAt(tokenAuthenticationFilter(), BasicAuthenticationFilter.class);

            security.exceptionHandling(handler -> handler.accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(authenticationEntryPoint));
            return security;
        });
    }

    @Bean
    public AuthenticationManagerImpl authenticationManager() {
        AuthenticationManagerImpl authenticationManager = new AuthenticationManagerImpl();
        authenticationManager
                .setAuthenticationProvider(new TokenAuthenticationProvider(userDetailsService));
        return authenticationManager;
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        TokenAuthenticationFilter filter =
                new TokenAuthenticationFilter(new AntPathRequestMatcher("/**"));
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationEntryPoint(authenticationEntryPoint);
        filter.setGetToken((request, response) -> {
            return ResponseToken.builder().accessToken(request.getHeader("Authorization")).build();
        });
        filter.setClearToken((request, response) -> {
            HttpEnhancer.create(request, response)
                    .delCookie(AuthenticationTokenUtils.ACCESS_TOKEN_COOKIE_NAME);
        });
        return filter;
    }
}
