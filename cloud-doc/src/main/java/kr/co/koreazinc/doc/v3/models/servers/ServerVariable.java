package kr.co.koreazinc.doc.v3.models.servers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ServerVariable
 *
 * @see     io.swagger.v3.oas.models.servers.ServerVariable
 * @see     io.swagger.v3.oas.annotations.servers.ServerVariable
 */

@Getter
@NoArgsConstructor
public class ServerVariable {
    
    @JsonProperty("enum")
    private List<String> enumValue = List.of();

    @JsonProperty("default")
    private String defaultValue;

    private String description;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}