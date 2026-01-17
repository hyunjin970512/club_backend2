package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.ParameterCategory;

@Component
@Converter(autoApply = true)
public class ParameterCategoryConverter extends EnumConverter<ParameterCategory> {

    @Override
    protected Class<ParameterCategory> getEnumClass() {
        return ParameterCategory.class;
    }
}
