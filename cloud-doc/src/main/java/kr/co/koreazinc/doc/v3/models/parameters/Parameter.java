package kr.co.koreazinc.doc.v3.models.parameters;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.co.koreazinc.doc.v3.models.examples.Example;
import kr.co.koreazinc.doc.v3.models.media.Content;
import kr.co.koreazinc.doc.v3.models.media.Schema;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Parameter
 *
 * @see     io.swagger.v3.oas.models.parameters.Parameter
 * @see     io.swagger.v3.oas.annotations.Parameter
 */

@Getter
@NoArgsConstructor
public class Parameter {

    public static final String PARAMETERS_SCHEMAS_REF = "#/paths/%s/%s/parameters/%s/schema/%s";

    public static String getSchemasName(String path, HttpMethod method, int index, String name) {
        return String.format(PARAMETERS_SCHEMAS_REF, path, method.name(), index, name);
    }

    private String name = UUID.randomUUID().toString();

    private String in;

    private String description;

    private boolean required = false;

    private boolean deprecated = false;

    private boolean allowEmptyValue = false;

    @JsonProperty("$ref")
    private String reference;

    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public enum StyleEnum {
        MATRIX("matrix"),
        LABEL("label"),
        FORM("form"),
        SIMPLE("simple"),
        SPACEDELIMITED("spaceDelimited"),
        PIPEDELIMITED("pipeDelimited"),
        DEEPOBJECT("deepObject");

        @JsonValue
        private String value;
    }

    private StyleEnum style;

    private boolean explode = false;

    private boolean allowReserved = false;

    private Schema schema;

    private Map<String, Example> examples = Map.of();

    private Object example;

    private Content content;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}
