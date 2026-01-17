package kr.co.koreazinc.data.model.converter;

public abstract class EntityConverter<O, E> {

    protected O origin;
    
    protected EntityConverter(O origin) {
        this.origin = origin;
    }

    protected abstract E toEntity();
}