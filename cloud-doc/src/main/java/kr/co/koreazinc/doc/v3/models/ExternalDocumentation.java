package kr.co.koreazinc.doc.v3.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ExternalDocumentation
 * Additional external documentation.
 *
 * @see     io.swagger.v3.oas.models.ExternalDocumentation
 * @see     io.swagger.v3.oas.annotations.ExternalDocumentation
 */

@Getter
@NoArgsConstructor
public class ExternalDocumentation {

    private String description;

    private String url;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}