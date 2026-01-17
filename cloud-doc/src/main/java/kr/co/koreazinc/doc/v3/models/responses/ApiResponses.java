package kr.co.koreazinc.doc.v3.models.responses;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ApiResponses
 *
 * @see     io.swagger.v3.oas.models.responses.ApiResponses
 * @see     io.swagger.v3.oas.annotations.responses.ApiResponses
 */

@Getter
@NoArgsConstructor
public class ApiResponses extends LinkedHashMap<String, ApiResponse> {

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}