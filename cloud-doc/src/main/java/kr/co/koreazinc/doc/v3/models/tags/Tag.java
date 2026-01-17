package kr.co.koreazinc.doc.v3.models.tags;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.ExternalDocumentation;
import kr.co.koreazinc.doc.v3.models.annotations.Customizing;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Tag
 *
 * @see     io.swagger.v3.oas.models.tags.Tag
 * @see     io.swagger.v3.oas.annotations.tags.Tags
 * @see     io.swagger.v3.oas.annotations.tags.Tag
 */

@Getter
@NoArgsConstructor
public class Tag implements Comparable<Tag> {

    // REQUIRED. The name of the tag.(Key)
    private String name;

    // A short description for the tag.(Markdown)
    private String description;

    // Additional external documentation for this tag.
    private ExternalDocumentation externalDocs;

    @Customizing
    private Integer order = 0;
    
    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public Tag setOrder(Integer order) {
        this.order = order;
        return this;
    }

    @Override
    public int compareTo(Tag o) {
        return this.name.compareTo(o.name);
    }
}