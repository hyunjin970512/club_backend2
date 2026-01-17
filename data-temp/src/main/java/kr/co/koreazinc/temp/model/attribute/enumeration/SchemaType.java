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
public enum SchemaType implements BaseEnum {
    $("@", I18N.builder().ko("스키마 타입").en("Component Category").zh("").ja("").build()), OBJECT(
            "object",
            I18N.builder().ko("객체").en("Object").zh("对象").ja("オブジェクト").build()), ARRAY("array",
                    I18N.builder().ko("배열").en("Array").zh("数组").ja("配列").build()), MAP("map",
                            I18N.builder().ko("맵").en("Map").zh("映射").ja("マップ").build()), STRING(
                                    "string",
                                    I18N.builder().ko("문자열").en("String").zh("字符串").ja(
                                            "文字列").build()), INTEGER("integer", I18N.builder()
                                                    .ko("정수").en("Integer").zh("整数").ja("整数")
                                                    .build()), NUMBER(
                                                            "number",
                                                            I18N.builder().ko("숫자").en("Number")
                                                                    .zh("数字").ja("数値")
                                                                    .build()), BOOLEAN(
                                                                            "boolean",
                                                                            I18N.builder().ko("논리")
                                                                                    .en("Boolean")
                                                                                    .zh("布尔值")
                                                                                    .ja("ブール値")
                                                                                    .build());

    @JsonValue
    private String value;

    private I18N name;

    public static SchemaType of(String value) {
        for (SchemaType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
