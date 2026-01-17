package kr.co.koreazinc.actuator.configuration;

import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.koreazinc.actuator.health.HealthProperties;
import kr.co.koreazinc.actuator.health.ServerComposite;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HealthConfig {

    private final HealthProperties properties;

    @Bean
    public CompositeHealthContributor server() {
        return new ServerComposite(properties);
    }
}