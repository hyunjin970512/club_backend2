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
public enum MenuType implements BaseEnum {
    $("@", I18N.builder().ko("매핑 타입").en("Mapping Type").zh("").ja("").build()), DIR("directory",
            I18N.builder().ko("디렉토리").en("Directory").zh("目录").ja("ディレクトリ").build()), VIEW("view",
                    I18N.builder().ko("화면").en("View").zh("视图").ja("ビュー").build()), FUN("function",
                            I18N.builder().ko("기능").en("Function").zh("功能").ja("機能").build());

    @JsonValue
    private String value;

    private I18N name;

    public static MenuType of(String value) {
        for (MenuType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
