package kr.co.koreazinc.spring.convert.converter;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class HttpStatusConverter implements Converter<String, HttpStatus> {

    @Override
    public HttpStatus convert(@NonNull String source) {
        return Optional.of(source).map(element->HttpStatus.valueOf(Integer.parseInt(element))).orElse(HttpStatus.SERVICE_UNAVAILABLE);
    }
}