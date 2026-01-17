package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.MappingType;

@Component
@Converter(autoApply = true)
public class MappingTypeConverter extends EnumConverter<MappingType> {

    @Override
    protected Class<MappingType> getEnumClass() {
        return MappingType.class;
    }
}
