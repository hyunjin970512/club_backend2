package kr.co.koreazinc.doc.v3.models.links;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.headers.Header;
import kr.co.koreazinc.doc.v3.models.servers.Server;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Link
 *
 * @see     io.swagger.v3.oas.models.links.Link
 * @see     io.swagger.v3.oas.annotations.links.Link
 */

@Getter
@NoArgsConstructor
public class Link {

    private String operationRef;

    private String operationId;

    private Map<String, String> parameters;

    private Object requestBody;

    @Deprecated
    private Map<String, Header> headers;

    private String description;

    @JsonProperty("$ref")
    private String reference;
    
    private Server server;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}