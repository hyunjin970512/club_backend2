package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.LinkType;

@Component
@Converter(autoApply = true)
public class LinkTypeConverter extends EnumConverter<LinkType> {

    @Override
    protected Class<LinkType> getEnumClass() {
        return LinkType.class;
    }
}
