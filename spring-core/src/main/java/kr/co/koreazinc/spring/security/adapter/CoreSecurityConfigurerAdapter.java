package kr.co.koreazinc.spring.security.adapter;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.ContentSecurityPolicyConfig;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HeaderSpec.ContentSecurityPolicySpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import kr.co.koreazinc.spring.http.matcher.PrefixPathRequestMatcher;
import kr.co.koreazinc.spring.property.PathProperty;
import kr.co.koreazinc.spring.support.SuppressWarning;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreSecurityConfigurerAdapter {

    private static SecurityProperties properties;

    @Autowired
    private void setProperties(SecurityProperties properties) {
        CoreSecurityConfigurerAdapter.properties = properties;
    }

    @FunctionalInterface
    public static interface ExFunction<T, R> {

        R apply(T t) throws Exception;
    }

    public static SecurityFilterChain init(HttpSecurity http, ExFunction<HttpSecurity, HttpSecurity> httpFunction) throws Exception {
        http.httpBasic(httpBasic->CoreSecurityConfigurerAdapter.HttpBasic.init(httpBasic))
            .csrf(csrf->CoreSecurityConfigurerAdapter.CSRF.init(csrf))
            .cors(cors->CoreSecurityConfigurerAdapter.CORS.init(cors))
            .headers(headers->CoreSecurityConfigurerAdapter.Headers.init(headers))
            .formLogin(login->login.disable())
            .logout(logout->logout.disable());

        return httpFunction.apply(http).build();
    }

    public static SecurityWebFilterChain init(ServerHttpSecurity http, Function<ServerHttpSecurity, ServerHttpSecurity> httpFunction) {
        http.httpBasic(httpBasic->CoreSecurityConfigurerAdapter.HttpBasic.init(httpBasic))
            .csrf(csrf->CoreSecurityConfigurerAdapter.CSRF.init(csrf))
            .cors(cors->CoreSecurityConfigurerAdapter.CORS.init(cors))
            .headers(headers->CoreSecurityConfigurerAdapter.Headers.init(headers))
            .formLogin(login->login.disable())
            .logout(logout->logout.disable());

        return httpFunction.apply(http).build();
    }

    public static class HttpBasic {

        public static void init(HttpBasicConfigurer<HttpSecurity> httpBasic) {
            httpBasic.disable();
        }

        public static void init(HttpBasicSpec httpBasic) {
            httpBasic.disable();
        }
    }

    public static class CSRF {

        public static void init(CsrfConfigurer<HttpSecurity> csrf) {
            csrf.disable();
        }

        public static void init(CsrfSpec csrf) {
            csrf.disable();
        }
    }

    public static class CORS {

        public static void init(CorsConfigurer<HttpSecurity> cors) {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of(CorsConfiguration.ALL));
            configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);
            org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            cors.configurationSource(source);
        }

        public static void init(CorsSpec cors) {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of(CorsConfiguration.ALL));
            configuration.setAllowedMethods(List.of("GET", "POST", "PATCH" , "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);
            org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            cors.configurationSource(source);
        }
    }

    public static class Headers {

        public static void init(HeadersConfigurer<HttpSecurity> headers) {
            headers.frameOptions(frameOptions->frameOptions.sameOrigin());
            if (CoreSecurityConfigurerAdapter.properties.getHeaders().hasContentSecurityPolicy()) {
                headers.contentSecurityPolicy(csp->CSPolicy.init(csp));
            }
        }

        public static void init(HeaderSpec headers) {
            headers.frameOptions(frameOptions->frameOptions.mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN));
            if (CoreSecurityConfigurerAdapter.properties.getHeaders().hasContentSecurityPolicy()) {
                headers.contentSecurityPolicy(csp->CSPolicy.init(csp));
            }
        }

        public static class CSPolicy {

            @SuppressWarnings(SuppressWarning.RAWTYPES)
            public static void init(ContentSecurityPolicyConfig contentSecurityPolicy) {
                contentSecurityPolicy.policyDirectives(CoreSecurityConfigurerAdapter.properties.getHeaders().getContentSecurityPolicy());
            }

            public static void init(ContentSecurityPolicySpec contentSecurityPolicy) {
                contentSecurityPolicy.policyDirectives(CoreSecurityConfigurerAdapter.properties.getHeaders().getContentSecurityPolicy());
            }
        }
    }

    public static class Request {

        public static AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry init(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeHttpRequests) {
            return authorizeHttpRequests.requestMatchers(org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(new PrefixPathRequestMatcher(PathProperty.RESOURCES)).permitAll()
                .requestMatchers(PathProperty.FAVICON, PathProperty.ROBOTS).permitAll()
                .requestMatchers(PathProperty.ERROR).permitAll();
        }

        public static AuthorizeExchangeSpec init(AuthorizeExchangeSpec authorizeExchange) {
            return authorizeExchange.matchers(org.springframework.boot.autoconfigure.security.reactive.PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .matchers(new PrefixPathRequestMatcher(PathProperty.RESOURCES)).permitAll()
                .pathMatchers(PathProperty.FAVICON, PathProperty.ROBOTS).permitAll()
                .pathMatchers(PathProperty.ERROR).permitAll();
        }
    }
}