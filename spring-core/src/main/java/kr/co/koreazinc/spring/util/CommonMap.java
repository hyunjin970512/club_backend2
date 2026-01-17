package kr.co.koreazinc.spring.util;

import java.util.HashMap;
import java.util.Map;

public class CommonMap extends HashMap<String, Object> {

    public CommonMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CommonMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CommonMap() {
        super();
    }

    public CommonMap(Map<String, Object> origin) {
        super(origin);
    }

    @Override
    public CommonMap put(String key, Object value) {
        super.put(key, value);
        return this; // this를 반환해야 체이닝 가능
    }

    // @Override
    // public Object get(Object key) {
    //     return super.get(key); // this를 반환해야 체이닝 가능
    // }
}