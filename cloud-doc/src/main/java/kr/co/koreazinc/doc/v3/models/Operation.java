package kr.co.koreazinc.doc.v3.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.annotations.Customizing;
import kr.co.koreazinc.doc.v3.models.callbacks.Callback;
import kr.co.koreazinc.doc.v3.models.parameters.Parameter;
import kr.co.koreazinc.doc.v3.models.parameters.RequestBody;
import kr.co.koreazinc.doc.v3.models.responses.ApiResponse;
import kr.co.koreazinc.doc.v3.models.responses.ApiResponses;
import kr.co.koreazinc.doc.v3.models.security.SecurityRequirement;
import kr.co.koreazinc.doc.v3.models.servers.Server;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Operation
 *
 * @see     io.swagger.v3.oas.models.Operation
 * @see     io.swagger.v3.oas.annotations.Operation
 */

@Slf4j
@Getter
@NoArgsConstructor
public class Operation {

    private List<String> tags = List.of();

    private String summary;

    private String description;

    private ExternalDocumentation externalDocs;

    private String operationId;

    private List<Parameter> parameters = List.of();

    private RequestBody requestBody = new RequestBody();

    private ApiResponses responses = new ApiResponses();

    private Map<String, Callback> callbacks = Map.of();

    private boolean deprecated = false;

    private List<SecurityRequirement> security = List.of();

    private List<Server> servers = List.of();

    @Customizing
    private String path;

    @Customizing
    private HttpMethod method;

    @Customizing
    private Integer order = 0;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public Operation setPath(String path) {
        this.path = path;
        return this;
    }

    public Operation setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Operation setOrder(Integer order) {
        this.order = order;
        return this;
    }

    public List<ApiResponse> getResponses() {
        List<ApiResponse> responses = new ArrayList<>();
        for (String status : this.responses.keySet()) {
            try {
                responses.add(this.responses.get(status).setStatus(Integer.parseInt(status)));
            } catch (NumberFormatException e) {
                log.warn("Invalid response status: {}", status, e);
            }
        }
        return responses;
    }
}