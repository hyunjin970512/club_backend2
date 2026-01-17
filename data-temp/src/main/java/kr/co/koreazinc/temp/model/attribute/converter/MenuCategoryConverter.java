package kr.co.koreazinc.temp.model.attribute.converter;

import org.springframework.stereotype.Component;

import jakarta.persistence.Converter;
import kr.co.koreazinc.data.support.attribute.EnumConverter;
import kr.co.koreazinc.temp.model.attribute.enumeration.MenuCategory;

@Component
@Converter(autoApply = true)
public class MenuCategoryConverter extends EnumConverter<MenuCategory> {

    @Override
    protected Class<MenuCategory> getEnumClass() {
        return MenuCategory.class;
    }
}
