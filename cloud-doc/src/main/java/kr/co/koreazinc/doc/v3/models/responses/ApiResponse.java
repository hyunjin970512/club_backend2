package kr.co.koreazinc.doc.v3.models.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.annotations.Customizing;
import kr.co.koreazinc.doc.v3.models.headers.Header;
import kr.co.koreazinc.doc.v3.models.links.Link;
import kr.co.koreazinc.doc.v3.models.media.Content;
import kr.co.koreazinc.doc.v3.models.media.MediaType;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ApiResponse
 *
 * @see     io.swagger.v3.oas.models.responses.ApiResponse
 * @see     io.swagger.v3.oas.annotations.responses.ApiResponse
 */

@Getter
@NoArgsConstructor
public class ApiResponse {

    private String description;

    private Map<String, Header> headers = Map.of();

    private Content content = new Content();

    private Map<String, Link> links = Map.of();

    @JsonProperty("$ref")
    private String reference;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    @Customizing
    private HttpStatus status;

    public ApiResponse setStatus(Integer status) {
        this.status = HttpStatus.resolve(status);
        return this;
    }

    public List<MediaType> getContent() {
        List<MediaType> mediaTypes = new ArrayList<>();
        for (String mediaType : this.content.keySet()) {
            mediaTypes.add(this.content.get(mediaType).setMediaType(mediaType));
        }
        return mediaTypes;
    }
}