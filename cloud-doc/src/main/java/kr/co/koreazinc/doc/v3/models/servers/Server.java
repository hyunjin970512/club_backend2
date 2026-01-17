package kr.co.koreazinc.doc.v3.models.servers;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Server
 * An array of Server Objects, which provide connectivity information to a target server.
 *
 * @see     io.swagger.v3.oas.models.servers.Server
 * @see     io.swagger.v3.oas.annotations.servers.Server
 */

@Getter
@NoArgsConstructor
public class Server {

    private String url;

    private String description;

    private ServerVariables variables;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}
