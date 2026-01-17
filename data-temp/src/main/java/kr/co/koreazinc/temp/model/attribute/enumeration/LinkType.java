package kr.co.koreazinc.temp.model.attribute.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

import kr.co.koreazinc.data.model.attribute.BaseEnum;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum LinkType implements BaseEnum {
    $("@", I18N.builder().ko("링크 종류").en("Link Type").zh("").ja("").build()), NONE("none",
            I18N.builder().ko("없음").en("None").zh("").ja("").build()), INNER("inner",
                    I18N.builder().ko("내부 페이지").en("Inner Page").zh("").ja("").build()), OUTER(
                            "outer",
                            I18N.builder().ko("외부 페이지").en("Outer Page").zh("").ja("").build()),;

    @JsonValue
    private String value;

    private I18N name;

    public static LinkType of(String value) {
        for (LinkType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
