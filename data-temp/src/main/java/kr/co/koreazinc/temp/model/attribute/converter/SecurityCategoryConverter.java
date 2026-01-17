package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.SecurityCategory;

@Component
@Converter(autoApply = true)
public class SecurityCategoryConverter extends EnumConverter<SecurityCategory> {

    @Override
    protected Class<SecurityCategory> getEnumClass() {
        return SecurityCategory.class;
    }
}
