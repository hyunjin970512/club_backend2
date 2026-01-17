package kr.co.koreazinc.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.koreazinc.spring.http.functional.HttpFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public HttpFunction.Servlet<MediaType> typeFunction() {
        return (request, response)->{
            return MediaType.APPLICATION_JSON;
        };
    }
}