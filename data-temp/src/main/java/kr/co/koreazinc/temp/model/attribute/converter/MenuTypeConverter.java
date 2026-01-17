package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.MenuType;

@Component
@Converter(autoApply = true)
public class MenuTypeConverter extends EnumConverter<MenuType> {

    @Override
    protected Class<MenuType> getEnumClass() {
        return MenuType.class;
    }
}
