package kr.co.koreazinc.doc.v3.models.headers;

import java.util.Map;

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
 * Header
 *
 * @see     io.swagger.v3.oas.models.headers.Header
 * @see     io.swagger.v3.oas.annotations.headers.Header
 */

@Getter
@NoArgsConstructor
public class Header {

    private String description;

    @JsonProperty("$ref")
    private String reference;

    private Boolean required;

    private Boolean deprecated;

    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static enum StyleEnum {
        SIMPLE("simple");

        @JsonValue
        private String value;
    }

    private StyleEnum style;

    private boolean explode = false;

    private Schema schema;

    private Map<String, Example> examples;

    private Object example;

    private Content content;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}