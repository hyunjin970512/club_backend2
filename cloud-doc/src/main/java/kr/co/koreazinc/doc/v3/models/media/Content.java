package kr.co.koreazinc.doc.v3.models.media;

import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Content
 *
 * @see     io.swagger.v3.oas.models.media.Content
 * @see     io.swagger.v3.oas.annotations.media.Content
 */

@Getter
@NoArgsConstructor
public class Content extends LinkedHashMap<String, MediaType> {
    
}