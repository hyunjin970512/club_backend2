package kr.co.koreazinc.spring.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public abstract class AbstractConverter<T> implements Converter<String, T> {

    public final T defaultValue;

    public AbstractConverter(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T convert(String source) {
        if ("null".equalsIgnoreCase(source) || "undefined".equalsIgnoreCase(source)) {
            return defaultValue;
        }
        return process(source);
    }

    public abstract T process(@NonNull String source);
}