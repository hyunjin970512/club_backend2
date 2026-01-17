package kr.co.koreazinc.data.support.attribute;

import java.util.Locale;

import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import kr.co.koreazinc.data.model.attribute.BaseEnum;

public abstract class EnumConverter<E extends BaseEnum> implements AttributeConverter<E, String>, Formatter<E>  {

    protected abstract Class<E> getEnumClass();

    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (ObjectUtils.isEmpty(attribute)) return null;
        return attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(String data) {
        if (StringUtils.hasText(data)) {
            for (E item : getEnumClass().getEnumConstants()) {
                if(item.getValue().equalsIgnoreCase(data)) return item;
            }
        }
        return null;
    }

    @Override
    public String print(E object, Locale locale) {
        return this.convertToDatabaseColumn(object);
    }

    @Override
    public E parse(String text, Locale locale) throws ParseException {
        return this.convertToEntityAttribute(text);
    }
}