package kr.co.koreazinc.data.support.attribute;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.querydsl.core.types.ExpressionException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConverterFactory {

    public final List<AttributeConverter<?, ?>> converters = new ArrayList<>();

    public ConverterFactory() {
        try {
            ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AnnotationTypeFilter(Converter.class));
            for (BeanDefinition bd : provider.findCandidateComponents("kr.co.koreazinc")) {
                converters.add((AttributeConverter<?, ?>) Class.forName(bd.getBeanClassName()).getDeclaredConstructor().newInstance());
            }
        } catch (ReflectiveOperationException e) {
            log.error("Failed to load converters", e);

        }
    }

    public AttributeConverter<?, ?> getConverter(Class<AttributeConverter<?, ?>> type) {
        return converters.stream().filter(c -> c.getClass().equals(type)).findFirst().orElseThrow(()->new ExpressionException("Converter not found"));
    }
}