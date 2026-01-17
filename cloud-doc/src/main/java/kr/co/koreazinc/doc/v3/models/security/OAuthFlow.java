package kr.co.koreazinc.doc.v3.models.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.annotations.Customizing;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuthFlow
 *
 * @see     io.swagger.v3.oas.models.security.OAuthFlow
 * @see     io.swagger.v3.oas.annotations.security.OAuthFlow
 */

@Getter
@NoArgsConstructor
public class OAuthFlow {

    @Customizing
    private String type;

    private String authorizationUrl;

    private String tokenUrl;

    private String refreshUrl;

    private Scopes scopes;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public OAuthFlow setType(String type) {
        this.type = type;
        return this;
    }
}