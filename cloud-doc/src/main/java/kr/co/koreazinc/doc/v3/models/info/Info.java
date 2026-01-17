package kr.co.koreazinc.doc.v3.models.info;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.models.annotations.OpenAPI31;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Info
 * Provides metadata about the API. The metadata MAY be used by tooling as required.
 *
 * @see     io.swagger.v3.oas.models.info.Info
 * @see     io.swagger.v3.oas.annotations.info.Info
 */

@Getter
@NoArgsConstructor
public class Info {

    private String title;

    private String description;

    private String termsOfService;

    private Contact contact;

    private License license;

    private String version;

    @OpenAPI31
    private String summary;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}
