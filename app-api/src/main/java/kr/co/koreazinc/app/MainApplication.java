package kr.co.koreazinc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import kr.co.koreazinc.spring.CustomBeanNameGenerator;

@EnableDiscoveryClient
@ConfigurationPropertiesScan("kr.co.koreazinc.**")
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class })
@ComponentScan(nameGenerator = CustomBeanNameGenerator.class, value = "kr.co.koreazinc.**")
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}