package kr.co.koreazinc.spring.configuration;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import kr.co.koreazinc.spring.CustomResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean("messageSourceProperties")
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(@Qualifier("messageSourceProperties") MessageSourceProperties properties, ResourceLoader resourceLoader) {
        CustomResourceBundleMessageSource messageSource = new CustomResourceBundleMessageSource();
        List<String> basenames = properties.getBasename();
        if (basenames != null) {
            basenames.add("classpath:/messages/core-messages");
            messageSource.setBasenames(basenames.toArray(String[]::new));
        }
        Charset encoding = properties.getEncoding();
        if (encoding != null) {
            messageSource.setDefaultEncoding(encoding.name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        messageSource.setResourceLoader(resourceLoader);
        return messageSource;
    }
}