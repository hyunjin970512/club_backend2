package kr.co.koreazinc.spring.security.adapter;

import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {

    private Headers headers = new Headers();

    @Getter
    @Setter
    public static class Headers {

        private Map<String, Set<String>> contentSecurityPolicy = Map.of();

        public boolean hasContentSecurityPolicy() {
            return contentSecurityPolicy != null && !contentSecurityPolicy.isEmpty();
        }

        public String getContentSecurityPolicy() {
            return contentSecurityPolicy.entrySet().stream()
                .map(entry->entry.getKey() + " " + String.join(" ", entry.getValue()))
                .reduce((a, b)->a + "; " + b)
                .get();
        }
    }
}