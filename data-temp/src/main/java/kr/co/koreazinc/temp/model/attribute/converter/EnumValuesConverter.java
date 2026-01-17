package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.ObjectConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.EnumValues;

@Component
@Converter(autoApply = true)
public class EnumValuesConverter extends ObjectConverter<EnumValues> {

    @Override
    protected Class<EnumValues> getObjectClass() {
        return EnumValues.class;
    }
}
