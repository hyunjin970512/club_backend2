package kr.co.koreazinc.data.support.attribute;

import java.util.Locale;

import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ObjectConverter<O> implements AttributeConverter<O, String>, Formatter<O>  {

    protected abstract Class<O> getObjectClass();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String writeValue(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("Error converting object to JSON", e);
        }
        return null;
    }

    private O readValue(String data) {
        try {
            return objectMapper.readValue(data, getObjectClass());
        } catch (Exception e) {
            log.error("Error converting JSON to object", e);
        }
        return null;
    }

    @Override
    public String convertToDatabaseColumn(O attribute) {
        if (attribute == null) return null;
        return writeValue(attribute);
    }

    @Override
    public O convertToEntityAttribute(String data) {
        if (StringUtils.hasText(data)) return readValue(data);
        return null;
    }

    @Override
    public String print(O object, Locale locale) {
        return this.convertToDatabaseColumn(object);
    }

    @Override
    public O parse(String text, Locale locale) throws ParseException {
        return this.convertToEntityAttribute(text);
    }
}