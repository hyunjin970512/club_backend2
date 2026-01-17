package kr.co.koreazinc.doc.v3.models;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Paths
 *
 * @see     io.swagger.v3.oas.models.Paths
 */

@Getter
@NoArgsConstructor
public class Paths extends LinkedHashMap<String, PathItem> {
    
    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}