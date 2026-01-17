package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.ObjectConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.SecurityScope;

@Component
@Converter(autoApply = true)
public class SecurityScopeConverter extends ObjectConverter<SecurityScope> {

    @Override
    protected Class<SecurityScope> getObjectClass() {
        return SecurityScope.class;
    }
}
