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
public enum MappingType implements BaseEnum {
    $("@", I18N.builder().ko("매핑 타입").en("Mapping Type").zh("").ja("").build()), UNIT("unit",
            I18N.builder().ko("단위").en("Unit").zh("单位").ja("ユニット").build()), GROUP("group",
                    I18N.builder().ko("그룹").en("Group").zh("组").ja("グループ").build());

    @JsonValue
    private String value;

    private I18N name;

    public static MappingType of(String value) {
        for (MappingType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
