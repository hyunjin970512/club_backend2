package kr.co.koreazinc.data.model.attribute.enumeration;

import java.util.Locale;
import java.util.Objects;

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
public enum Language implements BaseEnum {
    $  ("@",                    Locale.ROOT,    I18N.builder().ko("Language").en("").zh("").ja("").build()),
    KO (Locale.KOREAN.getLanguage(),  Locale.KOREAN,  I18N.builder().ko("국문").en("Korean").zh("").ja("").build()),
    EN (Locale.ENGLISH.getLanguage(), Locale.ENGLISH, I18N.builder().ko("영문").en("English").zh("").ja("").build());

    @JsonValue
    private String value;

    private Locale locale;

    private I18N name;

    public static Language ofLocale(Locale locale) {
        for (Language item : Language.values()) {
            if(Objects.equals(item.getLocale(), locale)) return item;
        }
        return Language.$;
    }
}