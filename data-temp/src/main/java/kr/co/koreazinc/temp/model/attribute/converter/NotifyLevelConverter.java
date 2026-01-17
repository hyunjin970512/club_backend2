package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.NotifyLevel;

@Component
@Converter(autoApply = true)
public class NotifyLevelConverter extends EnumConverter<NotifyLevel> {

    @Override
    protected Class<NotifyLevel> getEnumClass() {
        return NotifyLevel.class;
    }
}
