package kr.co.koreazinc.data.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.model.attribute.enumeration.Yn;
import kr.co.koreazinc.data.support.attribute.EnumConverter;

@Component
@Converter(autoApply = true)
public class YnConverter extends EnumConverter<Yn> {

    @Override
    protected Class<Yn> getEnumClass() {
        return Yn.class;
    }
}