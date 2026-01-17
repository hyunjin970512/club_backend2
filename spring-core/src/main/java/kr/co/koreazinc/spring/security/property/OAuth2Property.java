package kr.co.koreazinc.spring.security.property;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.http.utility.UriUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.oauth2")
public class OAuth2Property {

    private Tenant tenant;

    private Map<String, Provider> provider;

    private Client client;

    @Getter
    @Setter
    public static class Tenant {

        private String id;
    }

    @Getter
    @Setter
    public static class Provider {

        public static final String AUTH = "auth";

        private String baseUrl;

        private String authorizationUrl;

        private String tokenUrl;

        private String userInfoUrl;

        private String logoutUrl;

        private String scope;

        private String identifier;

        private String discoveryUrl;
    }

    @Getter
    @Setter
    public static class Client {

        private String id;

        private String url;

        private String secret;

        private Redirect redirect;

        @Getter
        @Setter
        public static class Redirect {

            private String loginUrl;

            private String logoutUrl;

            public String getLoginURL(HttpServletRequest request) {
                return this.loginUrl.replace("{URL}", UriUtils.toBaseURI(HttpEnhancer.create(request).getURI()));
            }

            public String getLogoutURL(HttpServletRequest request) {
                return this.logoutUrl.replace("{URL}", UriUtils.toBaseURI(HttpEnhancer.create(request).getURI()));
            }
        }
    }

    public Provider getProvider(String key) {
        if (this.provider == null || !this.provider.containsKey(key)) {
            throw new IllegalArgumentException("Unknown OAuth2 provider: " + key);
        }
        return this.provider.get(key);
    }

    @Getter
    @Builder
    public static class Credential {

        private String baseUrl;
        private String tokenUrl;
        private String clientId;
        private String clientUrl;
        private String clientSecret;
        private String scope;
        private String discoveryUrl;
    }

    public Credential getCredential(String key) {
        return Credential.builder()
            .baseUrl(getProvider(key).getBaseUrl())
            .tokenUrl(getProvider(key).getTokenUrl())
            .clientId(this.getClient().getId())
            .clientUrl(this.getClient().getUrl())
            .clientSecret(this.getClient().getSecret())
            .scope(getProvider(key).getScope())
            .discoveryUrl(getProvider(key).getDiscoveryUrl())
            .build();
    }
}