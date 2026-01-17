package kr.co.koreazinc.doc.v3.models.security;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Scopes
 *
 * @see     io.swagger.v3.oas.models.security.Scopes
 * @see     io.swagger.v3.oas.annotations.security.OAuthScope
 */

@Getter
@NoArgsConstructor
public class Scopes extends LinkedHashMap<String, String> {
    
    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}