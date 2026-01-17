package kr.co.koreazinc.actuator.profile;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.config.Config;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "pyroscope")
public class PyroscopeProperties {

    private boolean enabled = false;

    private String serverAddress;

    public boolean isEnabled() {
        return enabled;
    }

    public Config of(String applicationName) {
        return new Config.Builder()
            .setAgentEnabled(this.enabled)
            .setApplicationName(applicationName)
            .setProfilingEvent(EventType.ITIMER)
            .setFormat(Format.JFR)
            .setServerAddress(this.serverAddress)
            .build();
    }
}