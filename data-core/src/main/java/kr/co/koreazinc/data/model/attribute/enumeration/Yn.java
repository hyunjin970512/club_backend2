package kr.co.koreazinc.data.model.attribute.enumeration;

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
public enum Yn implements BaseEnum {
    $ ("@", I18N.builder().ko("YN").en("").zh("").ja("").build()),
    N ("N", I18N.builder().ko("N").en("N").zh("N").ja("N").build()),
    Y ("Y", I18N.builder().ko("Y").en("Y").zh("Y").ja("Y").build());

    @JsonValue
    private String value;
    
    private I18N name;

    public static Yn ofBoolean(boolean value) {
        if (value) return Yn.Y;
        return Yn.N;
    }
}