package kr.co.koreazinc.data.model.attribute.converter;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Component
@Converter(autoApply = true)
public class HttpMethodConverter implements AttributeConverter<HttpMethod, String> {

    @Override
    public String convertToDatabaseColumn(HttpMethod attribute) {
        if (ObjectUtils.isEmpty(attribute)) return null;
        return attribute.name();
    }

    @Override
    public HttpMethod convertToEntityAttribute(String dbData) {
        if (StringUtils.hasText(dbData)) {
            return HttpMethod.valueOf(dbData);
        }
        return null;
    }
}