package kr.co.koreazinc.doc.v3.models.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OAuthFlows
 *
 * @see     io.swagger.v3.oas.models.security.OAuthFlows
 * @see     io.swagger.v3.oas.annotations.security.OAuthFlows
 */

@Getter
@NoArgsConstructor
public class OAuthFlows {

    private OAuthFlow implicit;

    private OAuthFlow password;

    private OAuthFlow clientCredentials;

    private OAuthFlow authorizationCode;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}