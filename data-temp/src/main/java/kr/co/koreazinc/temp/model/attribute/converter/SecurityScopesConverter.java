package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.ObjectConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.SecurityScopes;

@Component
@Converter(autoApply = true)
public class SecurityScopesConverter extends ObjectConverter<SecurityScopes> {

    @Override
    protected Class<SecurityScopes> getObjectClass() {
        return SecurityScopes.class;
    }
}
