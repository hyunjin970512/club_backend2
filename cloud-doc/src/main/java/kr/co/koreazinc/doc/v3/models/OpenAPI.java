package kr.co.koreazinc.doc.v3.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.models.annotations.OpenAPI31;
import kr.co.koreazinc.doc.v3.models.info.Info;
import kr.co.koreazinc.doc.v3.models.media.MediaType;
import kr.co.koreazinc.doc.v3.models.media.Schema;
import kr.co.koreazinc.doc.v3.models.parameters.Parameter;
import kr.co.koreazinc.doc.v3.models.responses.ApiResponse;
import kr.co.koreazinc.doc.v3.models.security.SecurityRequirement;
import kr.co.koreazinc.doc.v3.models.security.SecurityScheme;
import kr.co.koreazinc.doc.v3.models.servers.Server;
import kr.co.koreazinc.doc.v3.models.tags.Tag;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Specification
 *
 * @see     io.swagger.v3.oas.models.OpenAPI
 * @see     io.swagger.v3.oas.annotations.OpenAPIDefinition
 */

@Getter
@NoArgsConstructor
public class OpenAPI {

    private String openapi = "3.0.1";

    private Info info = new Info();

    private ExternalDocumentation externalDocs = new ExternalDocumentation();

    private List<Server> servers = List.of();

    private List<SecurityRequirement> security = List.of();

    private List<Tag> tags = List.of();

    private Paths paths = new Paths();

    private Components components = new Components();

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    @OpenAPI31
    private String jsonSchemaDialect;

    @OpenAPI31
    private Map<String, PathItem> webhooks = Map.of();

    public List<Tag> getTags() {
        AtomicInteger index = new AtomicInteger(1);
        return this.tags.stream()
            .sorted()
            .map(tag->tag.setOrder(index.getAndIncrement()))
            .collect(Collectors.toList());
    }

    public List<Operation> getOperation() {
        List<Operation> operations = new ArrayList<>();
        for (String path : this.paths.keySet()) {
            PathItem item = this.paths.get(path);
            for (HttpMethod method : item.getOperations().keySet()) {
                operations.add(item.getOperations().get(method).setPath(path).setMethod(method));
            }
        }
        return operations;
    }

    public List<Schema> getSchemas() {
        List<Schema> schemas = new ArrayList<>();

        // Components
        Map<String, Schema> components = this.components.getSchemas();
        for (String name : components.keySet()) {
            Schema schema = components.get(name).setName(Components.getSchemasName(name));
            schemas.add(schema);
        }

        // Paths
        Paths paths = this.paths;
        for (String path : paths.keySet()) {
            Map<HttpMethod, Operation> operations = paths.get(path).getOperations();
            for (HttpMethod method : operations.keySet()) {
                Operation operation = operations.get(method);

                // Operation Parameters
                List<Parameter> parameters = operation.getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    Parameter parameter = parameters.get(i);
                    Schema schema = parameter.getSchema().setName(Parameter.getSchemasName(path, method, i, parameter.getName()));
                    if (schema == null || StringUtils.hasText(schema.getReference())) continue;
                    schemas.add(schema);
                }

                // Operation Request Body
                for (MediaType mediaType : operation.getRequestBody().getContent()) {
                    Schema schema = mediaType.getSchema().setName(MediaType.getRequestBodySchemasName(path, method, mediaType.getMediaType(), mediaType.getSchema().getName()));
                    if (schema == null || StringUtils.hasText(schema.getReference())) continue;
                    schemas.add(schema);
                }

                // Operation Responses
                for (ApiResponse response : operation.getResponses()) {
                    for (MediaType mediaType : response.getContent()) {
                        Schema schema = mediaType.getSchema().setName(MediaType.getResponseSchemasName(path, method, response.getStatus(), mediaType.getMediaType(), mediaType.getSchema().getName()));
                        if (schema == null || StringUtils.hasText(schema.getReference())) continue;
                        schemas.add(schema);
                    }
                }
            }
        }
        return schemas;
    }

    public Map<String, SecurityScheme> getSecuritySchemes() {
        return this.components.getSecuritySchemes();
    }
}