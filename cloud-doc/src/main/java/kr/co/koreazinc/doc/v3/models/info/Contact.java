package kr.co.koreazinc.doc.v3.models.info;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Contact
 *
 * @see     io.swagger.v3.oas.models.info.Contact
 * @see     io.swagger.v3.oas.annotations.info.Contact
 */

@Getter
@NoArgsConstructor
public class Contact {

    private String name;

    private String url;

    private String email;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}