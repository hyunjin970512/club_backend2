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
public enum MenuCategory implements BaseEnum {
    $("@", I18N.builder().ko("메뉴 분류").en("Menu Category").zh("").ja("").build()), ADMIN("admin",
            I18N.builder().ko("플랫폼").en("Platform").zh("平台").ja("プラットフォーム").build()), PORTAL(
                    "portal", I18N.builder().ko("포털").en("Portal").zh("门户").ja("ポータル").build());

    @JsonValue
    private String value;

    private I18N name;

    public static MenuCategory of(String value) {
        for (MenuCategory category : values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }
}
