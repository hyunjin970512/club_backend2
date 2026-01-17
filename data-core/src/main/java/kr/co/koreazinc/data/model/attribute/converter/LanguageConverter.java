package kr.co.koreazinc.data.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.model.attribute.enumeration.Language;
import kr.co.koreazinc.data.support.attribute.EnumConverter;

@Component
@Converter(autoApply = true)
public class LanguageConverter extends EnumConverter<Language> {

    @Override
    protected Class<Language> getEnumClass() {
        return Language.class;
    }
}