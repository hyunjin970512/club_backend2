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
public enum IndexType implements BaseEnum {
    $("@", I18N.builder().ko("Index Type").en("Index Type").zh("").ja("").build()), TAG("tag",
            I18N.builder().ko("Tag").en("Tag").zh("Tag").ja("Tag").build()), OPT("operation",
                    I18N.builder().ko("Operation").en("Operation").zh("Operation").ja("Operation")
                            .build());

    @JsonValue
    private String value;

    private I18N name;
}
