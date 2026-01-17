package kr.co.koreazinc.data.functional;

import org.hibernate.Incubating;

@Incubating
@FunctionalInterface
public interface NativeFormer<T> {

    String transformAlias(String alias) throws ReflectiveOperationException;
}