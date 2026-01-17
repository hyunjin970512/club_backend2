package kr.co.koreazinc.doc.v3.models.security;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SecurityScheme
 *
 * @see     io.swagger.v3.oas.models.security.SecurityScheme
 * @see     io.swagger.v3.oas.annotations.security.SecurityScheme
 */

@Getter
@NoArgsConstructor
public class SecurityScheme {

    private Type type;

    private String description;

    private String name;

    @JsonProperty("$ref")
    private String reference;

    private In in;

    private String scheme;

    private String bearerFormat;

    private OAuthFlows flows = new OAuthFlows();

    private String openIdConnectUrl;

    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public enum Type {
        APIKEY("apiKey"),
        HTTP("http"),
        OAUTH2("oauth2"),
        OPENIDCONNECT("openIdConnect"),
        MUTUALTLS("mutualTLS");

        @JsonValue
        private String value;
    }

    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public enum In {
        COOKIE("cookie"),
        HEADER("header"),
        QUERY("query");

        @JsonValue
        private String value;
    }

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public List<OAuthFlow> getFlows() {
        List<OAuthFlow> flows = new ArrayList<>();
        if (Type.OAUTH2.equals(this.type)) {
            if (this.flows.getImplicit() != null) {
                flows.add(this.flows.getImplicit().setType("implicit"));
            }
            if (this.flows.getPassword() != null) {
                flows.add(this.flows.getPassword().setType("password"));
            }
            if (this.flows.getClientCredentials() != null) {
                flows.add(this.flows.getClientCredentials().setType("clientCredentials"));
            }
            if (this.flows.getAuthorizationCode() != null) {
                flows.add(this.flows.getAuthorizationCode().setType("authorizationCode"));
            }
        }
        return flows;
    }
}