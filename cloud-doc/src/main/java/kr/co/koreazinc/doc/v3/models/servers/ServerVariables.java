package kr.co.koreazinc.doc.v3.models.servers;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ServerVariables
 *
 * @see     io.swagger.v3.oas.models.servers.ServerVariables
 * @see     io.swagger.v3.oas.annotations.servers.SecurityRequirement
 */

@Getter
@NoArgsConstructor
public class ServerVariables extends LinkedHashMap<String, ServerVariable> {
    
    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}