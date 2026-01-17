package kr.co.koreazinc.data.model.attribute.converter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Component
@Converter(autoApply = true)
public class HttpStatusConverter implements AttributeConverter<HttpStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(HttpStatus attribute) {
        if (ObjectUtils.isEmpty(attribute)) return null;
        return attribute.value();
    }

    @Override
    public HttpStatus convertToEntityAttribute(Integer dbData) {
        return HttpStatus.resolve(dbData);
    }
}