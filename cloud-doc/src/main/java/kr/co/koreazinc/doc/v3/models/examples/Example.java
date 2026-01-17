package kr.co.koreazinc.doc.v3.models.examples;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Example
 *
 * @see     io.swagger.v3.oas.models.examples.Example
 */

@Getter
@NoArgsConstructor
public class Example {

    private String summary;

    private String description;

    private Object value;

    private String externalValue;

    @JsonProperty("$ref")
    private String reference;

    private boolean valueSetFlag = false;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}