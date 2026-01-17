package kr.co.koreazinc.actuator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.pyroscope.javaagent.PyroscopeAgent;
import jakarta.annotation.PostConstruct;
import kr.co.koreazinc.actuator.profile.PyroscopeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PyroscopeConfig {

    private final PyroscopeProperties properties;

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init() {
        if (properties.isEnabled()) {
            PyroscopeAgent.start(properties.of(applicationName));
        }
    }
}