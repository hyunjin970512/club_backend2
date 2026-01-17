package kr.co.koreazinc.doc.v3.converter;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnumValueConverter implements ModelConverter {
    
    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        return chain.hasNext() ? chain.next().resolve(type, context, chain) : null;
    }
}
