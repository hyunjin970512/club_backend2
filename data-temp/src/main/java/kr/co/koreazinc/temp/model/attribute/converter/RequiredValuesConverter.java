package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.ObjectConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.RequiredValues;

@Component
@Converter(autoApply = true)
public class RequiredValuesConverter extends ObjectConverter<RequiredValues> {

    @Override
    protected Class<RequiredValues> getObjectClass() {
        return RequiredValues.class;
    }
}
