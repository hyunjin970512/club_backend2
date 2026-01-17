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
public enum ParameterCategory implements BaseEnum {
    $("@", I18N.builder().ko("파라미터 분류").en("Parameter Category").zh("").ja("").build()), PATH(
            "path", I18N.builder().ko("경로").en("Path").zh("路径").ja("パス").build()), QUERY("query",
                    I18N.builder().ko("쿼리").en("Query").zh("查询").ja("クエリ").build()), HEADER(
                            "header",
                            I18N.builder().ko("헤더").en("Header").zh("头部").ja("ヘッダー")
                                    .build()), COOKIE("cookie",
                                            I18N.builder().ko("쿠키").en("Cookie").zh("饼干").ja("クッキー")
                                                    .build()),;

    @JsonValue
    private String value;

    private I18N name;

    public static ParameterCategory of(String value) {
        for (ParameterCategory category : values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }
}
