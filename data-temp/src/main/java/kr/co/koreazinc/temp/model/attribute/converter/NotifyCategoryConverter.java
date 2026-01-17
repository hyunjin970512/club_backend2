package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.NotifyCategory;

@Component
@Converter(autoApply = true)
public class NotifyCategoryConverter extends EnumConverter<NotifyCategory> {

    @Override
    protected Class<NotifyCategory> getEnumClass() {
        return NotifyCategory.class;
    }
}
