package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.IndexType;

@Component
@Converter(autoApply = true)
public class IndexTypeConverter extends EnumConverter<IndexType> {

    @Override
    protected Class<IndexType> getEnumClass() {
        return IndexType.class;
    }
}
