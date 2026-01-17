package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.SchemaType;

@Component
@Converter(autoApply = true)
public class SchemaTypeConverter extends EnumConverter<SchemaType> {

    @Override
    protected Class<SchemaType> getEnumClass() {
        return SchemaType.class;
    }
}
