package kr.co.koreazinc.doc.v3.models.media;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.co.koreazinc.doc.v3.models.headers.Header;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Encoding
 *
 * @see     io.swagger.v3.oas.models.media.Encoding
 */

@Getter
@NoArgsConstructor
public class Encoding {

    private String contentType;

    private Map<String, Header> headers;

    private StyleEnum style;

    private Boolean explode;

    private Boolean allowReserved;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static enum StyleEnum {
        FORM("form"),
        SPACE_DELIMITED("spaceDelimited"),
        PIPE_DELIMITED("pipeDelimited"),
        DEEP_OBJECT("deepObject");

        @JsonValue
        private String value;
    }

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();
}