package kr.co.koreazinc.doc.v3.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Extensions implements Map<String, Object> {

    private final Map<String, Object> extensions = new HashMap<>();

    public static final String NAME = "extensions";

    public static class Attribute {

        // 아이디
        public static final String ID = "x-ATTR-ID";

        // 상위 아이디
        public static final String UP_ID = "x-ATTR-UP_ID";

        // 코드
        public static final String CD = "x-ATTR-CD";

        // 상위 코드
        public static final String UP_CD = "x-ATTR-UP_CD";

        public static final String TAG_CD = "x-ATTR-TAG_CD";

        public static final String ORDER = "x-ATTR-ORDER";

        public static class Name {

            // 한글명
            public static final String KO = "x-ATTR-NAME-KO";

            // 영문명
            public static final String EN = "x-ATTR-NAME-EN";

            // 중문명
            public static final String ZH = "x-ATTR-NAME-ZH";

            // 일문명
            public static final String JA = "x-ATTR-NAME-JA";
        }
    }

    @Override
    public int size() {
        return this.extensions.size();
    }

    @Override
    public boolean isEmpty() {
        return this.extensions.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.extensions.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.extensions.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.extensions.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return this.extensions.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return this.extensions.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        this.extensions.putAll(m);
    }

    @Override
    public void clear() {
        this.extensions.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.extensions.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.extensions.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.extensions.entrySet();
    }

    public String asText(String key) {
        if (this.extensions.containsKey(key)) {
            return String.valueOf(this.extensions.get(key));
        }
        return null;
    }

    public String asTextOrDefault(String key, String defaultValue) {
        if (this.extensions.containsKey(key)) {
            return this.asText(key);
        }
        return defaultValue;
    }

    public Integer asInt(String key) {
        if (this.extensions.containsKey(key)) {
            return (Integer) this.extensions.get(key);
        }
        return null;
    }

    public Integer asIntOrDefault(String key, Integer defaultValue) {
        if (this.extensions.containsKey(key)) {
            return this.asInt(key);
        }
        return defaultValue;
    }
}