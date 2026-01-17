package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.SecurityFlowType;

@Component
@Converter(autoApply = true)
public class SecurityFlowTypeConverter extends EnumConverter<SecurityFlowType> {

    @Override
    protected Class<SecurityFlowType> getEnumClass() {
        return SecurityFlowType.class;
    }
}
