package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.SecurityType;

@Component
@Converter(autoApply = true)
public class SecurityTypeConverter extends EnumConverter<SecurityType> {

    @Override
    protected Class<SecurityType> getEnumClass() {
        return SecurityType.class;
    }
}
