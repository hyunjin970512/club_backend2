package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.NotifyStatus;

@Component
@Converter(autoApply = true)
public class NotifyStatusConverter extends EnumConverter<NotifyStatus> {

    @Override
    protected Class<NotifyStatus> getEnumClass() {
        return NotifyStatus.class;
    }
}
