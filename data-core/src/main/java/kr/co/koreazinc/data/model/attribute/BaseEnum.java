package kr.co.koreazinc.data.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;

import kr.co.koreazinc.data.model.attribute.enumeration.Yn;
import kr.co.koreazinc.data.model.embedded.piece.I18N;
import kr.co.koreazinc.data.model.embedded.piece.Use;

public interface BaseEnum {

    @JsonValue
    public String getValue();

    public I18N getName();

    public int ordinal();

    public default Use getUse() {
        return Use.builder().yn(Yn.Y).build();
    }

    public static interface Setter {

        public void setClassName(String className);

        public void setCode(String code);

        public void setCodeName(I18N codeName);

        public void setOrder(Integer order);
    }
}