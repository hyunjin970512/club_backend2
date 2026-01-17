package kr.co.koreazinc.doc.v3.models.info;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.models.annotations.OpenAPI31;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * License
 *
 * @see     io.swagger.v3.oas.models.info.License
 * @see     io.swagger.v3.oas.annotations.info.License
 */

@Getter
@NoArgsConstructor
public class License {

    private String name;

    private String url;

    @OpenAPI31
    private String identifier;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}
