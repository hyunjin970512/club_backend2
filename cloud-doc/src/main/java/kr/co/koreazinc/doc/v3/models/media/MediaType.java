package kr.co.koreazinc.doc.v3.models.media;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.annotations.Customizing;
import kr.co.koreazinc.doc.v3.models.examples.Example;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MediaType
 *
 * @see     io.swagger.v3.oas.models.media.MediaType
 */

@Getter
@NoArgsConstructor
public class MediaType {

    public static final String REQUEST_BODY_SCHEMAS_REF = "#/paths/%s/%s/requestBody/content/%s/schema/%s";

    public static final String RESPONSE_SCHEMAS_REF = "#/paths/%s/%s/responses/%s/content/%s/schema/%s";

    public static String getRequestBodySchemasName(String path, HttpMethod method, String mediaType, String name) {
        return String.format(REQUEST_BODY_SCHEMAS_REF, path, method.name(), mediaType, name);
    }

    public static String getResponseSchemasName(String path, HttpMethod method, HttpStatus status, String mediaType, String name) {
        return String.format(RESPONSE_SCHEMAS_REF, path, method.name(), status.value(), mediaType, name);
    }

    private Schema schema = new Schema();

    private Map<String, Example> examples;

    private Object example;

    private Map<String, Encoding> encoding;

    private boolean exampleSetFlag = false;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    @Customizing
    private String mediaType;

    public MediaType setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}