package kr.co.koreazinc.spring.convert.converter;

import java.util.Optional;

import org.springframework.lang.NonNull;

import kr.co.koreazinc.spring.convert.AbstractConverter;


public class StringConverter extends AbstractConverter<String> {

    public StringConverter() {
        super(null);
    }

    @Override
    public String process(@NonNull String source) {
        return Optional.of(source).orElse(null);
    }
}