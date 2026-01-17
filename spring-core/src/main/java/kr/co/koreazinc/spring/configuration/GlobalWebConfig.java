package kr.co.koreazinc.spring.configuration;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.util.DigestUtils;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.koreazinc.spring.convert.converter.HttpStatusConverter;
import kr.co.koreazinc.spring.convert.converter.StringConverter;
import kr.co.koreazinc.spring.convert.converter.UUIDConverter;
import kr.co.koreazinc.spring.http.codec.Jackson2XmlDecoder;
import kr.co.koreazinc.spring.http.codec.Jackson2XmlEncoder;
import kr.co.koreazinc.spring.property.PathProperty;
import kr.co.koreazinc.spring.property.SpringProperty;
import kr.co.koreazinc.spring.web.servlet.context.DomainContextMatcher;
import lombok.RequiredArgsConstructor;

public class GlobalWebConfig {

    /* Resource 설정 */
    private static final String[] ROOT_LOCATIONS = { PathProperty.FAVICON, PathProperty.ROBOTS };

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/static/" };

    private static final String[] CLASSPATH_RESOURCE_COMPONENT_LOCATIONS = { "classpath:/component/" };

    private static final String[] CLASSPATH_RESOURCE_MODULE_LOCATIONS = { "classpath:/templates/" };

    /* Filter 설정 */
    public static final String[] API_PATHS = {
        PathProperty.API.V1 + "/*",
        PathProperty.API.V2 + "/*",
        PathProperty.API.V3 + "/*",
    };

    /* Resolver 설정 */
    public static final String LOCALE_COOKIE_NAME = "APPLICATION_LOCALE";

    @RequiredArgsConstructor
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class GlobalWebServletConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(@NonNull org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
            for(String path : GlobalWebConfig.ROOT_LOCATIONS) {
                registry.addResourceHandler(path).addResourceLocations("classpath:/");
            }
            registry.addResourceHandler(PathProperty.RESOURCES_COMPONENT + "/**")
                    .addResourceLocations(GlobalWebConfig.CLASSPATH_RESOURCE_COMPONENT_LOCATIONS)
                    .setUseLastModified(false)
                    .setEtagGenerator((resource)->{
                        if (SpringProperty.IS_LOCAL) return null;
                        try {
                            return DigestUtils.md5DigestAsHex(resource.getInputStream());
                        } catch (IOException e) {  }
                        return null;
                    })
                    .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler(PathProperty.RESOURCES_MODULE + "/**")
                    .addResourceLocations(GlobalWebConfig.CLASSPATH_RESOURCE_MODULE_LOCATIONS)
                    .setUseLastModified(false)
                    .setEtagGenerator((resource)->{
                        if (SpringProperty.IS_LOCAL) return null;
                        try {
                            return DigestUtils.md5DigestAsHex(resource.getInputStream());
                        } catch (IOException e) {  }
                        return null;
                    })
                    .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler(PathProperty.RESOURCES + "/**")
                    .addResourceLocations(GlobalWebConfig.CLASSPATH_RESOURCE_LOCATIONS)
                    .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                    .resourceChain(!SpringProperty.IS_LOCAL)
                    .addResolver(new org.springframework.web.servlet.resource.VersionResourceResolver().addContentVersionStrategy("/**"))
                    .addTransformer(new org.springframework.web.servlet.resource.CssLinkResourceTransformer());

            registry.setOrder(Ordered.LOWEST_PRECEDENCE);
        }

        @Override
        public void addFormatters(@NonNull FormatterRegistry registry) {
            registry.addConverter(new StringConverter());
            registry.addConverter(new UUIDConverter());
            registry.addConverter(new HttpStatusConverter());
        }

        public org.springframework.web.servlet.HandlerInterceptor localeChangeInterceptor() {
            org.springframework.web.servlet.i18n.LocaleChangeInterceptor localeChangeInterceptor = new org.springframework.web.servlet.i18n.LocaleChangeInterceptor();
            localeChangeInterceptor.setParamName("lang");
            localeChangeInterceptor.setIgnoreInvalidLocale(true);
            return localeChangeInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(localeChangeInterceptor());
        }

        @Bean
        public org.springframework.web.servlet.LocaleResolver localeResolver() {
            org.springframework.web.servlet.i18n.CookieLocaleResolver localeResolver = new org.springframework.web.servlet.i18n.CookieLocaleResolver(GlobalWebConfig.LOCALE_COOKIE_NAME);
            localeResolver.setLanguageTagCompliant(false);
            localeResolver.setDefaultLocale(Locale.KOREAN);
            return localeResolver;
        }

        @Bean
        public kr.co.koreazinc.spring.web.servlet.context.ContextResolver corpResolver(@Autowired(required = false) DomainContextMatcher matcher) {
            return new kr.co.koreazinc.spring.web.servlet.context.DomainContextResolver(matcher);
        }
    }

    @RequiredArgsConstructor
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = Type.REACTIVE)
    public static class GlobalWebReactiveConfig implements WebFluxConfigurer {

        @Override
        public void addResourceHandlers(@NonNull org.springframework.web.reactive.config.ResourceHandlerRegistry registry) {
            for(String path : GlobalWebConfig.ROOT_LOCATIONS) {
                registry.addResourceHandler(path).addResourceLocations("classpath:/");
            }
            registry.addResourceHandler(PathProperty.RESOURCES_COMPONENT + "/**")
                    .addResourceLocations(GlobalWebConfig.CLASSPATH_RESOURCE_COMPONENT_LOCATIONS)
                    .setUseLastModified(false)
                    .setEtagGenerator((resource)->{
                        if (SpringProperty.IS_LOCAL) return null;
                        try {
                            return DigestUtils.md5DigestAsHex(resource.getInputStream());
                        } catch (IOException e) {  }
                        return null;
                    })
                    .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler(PathProperty.RESOURCES_MODULE + "/**")
                    .addResourceLocations(GlobalWebConfig.CLASSPATH_RESOURCE_MODULE_LOCATIONS)
                    .setUseLastModified(false)
                    .setEtagGenerator((resource)->{
                        if (SpringProperty.IS_LOCAL) return null;
                        try {
                            return DigestUtils.md5DigestAsHex(resource.getInputStream());
                        } catch (IOException e) {  }
                        return null;
                    })
                    .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler(PathProperty.RESOURCES + "/**")
                    .addResourceLocations(GlobalWebConfig.CLASSPATH_RESOURCE_LOCATIONS)
                    .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                    .resourceChain(!SpringProperty.IS_LOCAL)
                    .addResolver(new org.springframework.web.reactive.resource.VersionResourceResolver().addContentVersionStrategy("/**"))
                    .addTransformer(new org.springframework.web.reactive.resource.CssLinkResourceTransformer());

            registry.setOrder(Ordered.LOWEST_PRECEDENCE);
        }

        @Override
        public void addFormatters(@NonNull FormatterRegistry registry) {
            registry.addConverter(new StringConverter());
            registry.addConverter(new UUIDConverter());
            registry.addConverter(new HttpStatusConverter());
        }

        @Override
        public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer configurer) {
            configurer.customCodecs().register(new Jackson2XmlDecoder());
            configurer.customCodecs().register(new Jackson2XmlEncoder());
        }

        @Bean
        public kr.co.koreazinc.spring.web.reactive.HandlerInterceptor localeChangeInterceptor() {
            kr.co.koreazinc.spring.web.reactive.i18n.LocaleChangeInterceptor localeChangeInterceptor = new kr.co.koreazinc.spring.web.reactive.i18n.LocaleChangeInterceptor();
            localeChangeInterceptor.setParamName("lang");
            localeChangeInterceptor.setIgnoreInvalidLocale(true);
            return localeChangeInterceptor;
        }

        @Bean
        public org.springframework.web.server.i18n.LocaleContextResolver localeContextResolver() {
            kr.co.koreazinc.spring.web.reactive.i18n.CookieLocaleResolver localeResolver = new kr.co.koreazinc.spring.web.reactive.i18n.CookieLocaleResolver(GlobalWebConfig.LOCALE_COOKIE_NAME);
            localeResolver.setLanguageTagCompliant(false);
            localeResolver.setDefaultLocale(Locale.KOREAN);
            return localeResolver;
        }

        // TODO Corporation Interceptor 설정
        //
    }
}