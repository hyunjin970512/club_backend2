package kr.co.koreazinc.doc.v3.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.models.annotations.OpenAPI31;
import io.swagger.v3.oas.models.headers.Header;
import kr.co.koreazinc.doc.v3.models.callbacks.Callback;
import kr.co.koreazinc.doc.v3.models.examples.Example;
import kr.co.koreazinc.doc.v3.models.links.Link;
import kr.co.koreazinc.doc.v3.models.media.Schema;
import kr.co.koreazinc.doc.v3.models.parameters.Parameter;
import kr.co.koreazinc.doc.v3.models.parameters.RequestBody;
import kr.co.koreazinc.doc.v3.models.responses.ApiResponse;
import kr.co.koreazinc.doc.v3.models.security.SecurityScheme;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Components
 *
 * @see     io.swagger.v3.oas.models.Components
 */

@Getter
@NoArgsConstructor
public class Components {

    public static final String COMPONENTS_SCHEMAS_REF = "#/components/schemas/%s";

    public static String getSchemasName(String name) {
        return String.format(COMPONENTS_SCHEMAS_REF, name);
    }

    private Map<String, Schema> schemas = Map.of();

    private Map<String, ApiResponse> responses = Map.of();

    private Map<String, Parameter> parameters = Map.of();

    private Map<String, Example> examples = Map.of();

    private Map<String, RequestBody> requestBodies = Map.of();

    private Map<String, Header> headers = Map.of();

    private Map<String, SecurityScheme> securitySchemes = Map.of();

    private Map<String, Link> links = Map.of();

    private Map<String, Callback> callbacks = Map.of();

    @OpenAPI31
    private Map<String, PathItem> pathItems = Map.of();

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}