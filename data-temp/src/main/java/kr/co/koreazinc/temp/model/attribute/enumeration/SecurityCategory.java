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
public enum SecurityCategory implements BaseEnum {
    $("@", I18N.builder().ko("보안 분류").en("Security Category").zh("").ja("").build()), COOKIE(
            "cookie",
            I18N.builder().ko("쿠키").en("Cookie").zh("Cookie").ja("クッキー").build()), HEADER("header",
                    I18N.builder().ko("헤더").en("Header").zh("头部").ja("ヘッダー").build()), QUERY(
                            "query",
                            I18N.builder().ko("쿼리").en("Query").zh("查询").ja("クエリ").build());

    @JsonValue
    private String value;

    private I18N name;

    public static SecurityCategory of(String value) {
        for (SecurityCategory category : values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }
}
