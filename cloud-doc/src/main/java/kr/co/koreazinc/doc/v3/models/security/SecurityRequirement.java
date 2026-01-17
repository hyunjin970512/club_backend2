package kr.co.koreazinc.doc.v3.models.security;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SecurityRequirement
 *
 * @see     io.swagger.v3.oas.models.security.SecurityRequirement
 * @see     io.swagger.v3.oas.annotations.security.SecurityRequirement
 */

@Getter
@NoArgsConstructor
public class SecurityRequirement extends LinkedHashMap<String, List<String>> {
    
}