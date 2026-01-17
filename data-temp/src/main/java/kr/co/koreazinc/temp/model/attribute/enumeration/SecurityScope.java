package kr.co.koreazinc.temp.model.attribute.enumeration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityScope implements Map<String, String> {

    private final Map<String, String> values = new HashMap<>();

    public SecurityScope(Map<String, String> values) {
        if (values != null) {
            this.values.putAll(values);
        }
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.values.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return this.values.get(key);
    }

    @Override
    public String put(String key, String value) {
        return this.values.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return this.values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        this.values.putAll(m);
    }

    @Override
    public void clear() {
        this.values.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.values.keySet();
    }

    @Override
    public Collection<String> values() {
        return this.values.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.values.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.values.equals(o);
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }
}
