package kr.co.koreazinc.doc.v3.models.parameters;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.media.Content;
import kr.co.koreazinc.doc.v3.models.media.MediaType;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RequestBody
 *
 * @see     io.swagger.v3.oas.models.parameters.RequestBody
 * @see     io.swagger.v3.oas.annotations.parameters.RequestBody
 */

@Getter
@NoArgsConstructor
public class RequestBody {

    private String description;

    private Content content = new Content();

    private boolean required = false;

    @JsonProperty("$ref")
    private String reference;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public List<MediaType> getContent() {
        List<MediaType> mediaTypes = new ArrayList<>();
        for (String mediaType : this.content.keySet()) {
            mediaTypes.add(this.content.get(mediaType).setMediaType(mediaType));
        }
        return mediaTypes;
    }
}