package kr.co.koreazinc.data.model.attribute.converter;

import java.lang.reflect.Constructor;

import kr.co.koreazinc.data.model.attribute.BaseEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseEnumConverter {

    private BaseEnum baseEnum;

    public BaseEnumConverter(BaseEnum baseEnum) {
        this.baseEnum = baseEnum;
    }

    public <T extends BaseEnum.Setter> T to(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            T dto = constructor.newInstance();
            dto.setClassName(baseEnum.getClass().getName());
            dto.setCode(baseEnum.getValue());
            dto.setCodeName(baseEnum.getName());
            dto.setOrder(baseEnum.ordinal());
            return dto;
        } catch (RuntimeException | ReflectiveOperationException e) {
            log.error("Error converting BaseEnum to DTO: {}", e.getMessage(), e);
        }
        return null;
    }
}