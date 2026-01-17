package kr.co.koreazinc.actuator.health;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "health")
public class HealthProperties {

    private List<Server> server;

    @Getter
    @Setter
    public static class Server {

        private String name;

        private String host;

        private int port;

        private int timeout = 1000;
    }
}