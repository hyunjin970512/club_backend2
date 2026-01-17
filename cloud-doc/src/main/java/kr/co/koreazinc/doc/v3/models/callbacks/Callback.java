package kr.co.koreazinc.doc.v3.models.callbacks;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.PathItem;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Callback
 *
 * @see     io.swagger.v3.oas.models.callbacks.Callback
 */

@Getter
@NoArgsConstructor
public class Callback extends LinkedHashMap<String, PathItem> {

    @JsonProperty("$ref")
    private String reference;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}