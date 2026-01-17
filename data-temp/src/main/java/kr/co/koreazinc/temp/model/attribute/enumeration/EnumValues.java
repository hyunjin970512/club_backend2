package kr.co.koreazinc.temp.model.attribute.enumeration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EnumValues implements List<String> {

    private final List<String> values = new ArrayList<>();

    public EnumValues(Collection<String> values) {
        if (values != null) {
            this.values.addAll(values);
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
    public boolean contains(Object o) {
        return this.values.contains(o);
    }

    @Override
    public Iterator<String> iterator() {
        return this.values.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.values.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.values.toArray(a);
    }

    @Override
    public boolean add(String e) {
        return this.values.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.values.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.values.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        return this.values.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        return this.values.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.values.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.values.retainAll(c);
    }

    @Override
    public void clear() {
        this.values.clear();
    }

    @Override
    public String get(int index) {
        return this.values.get(index);
    }

    @Override
    public String set(int index, String element) {
        return this.values.set(index, element);
    }

    @Override
    public void add(int index, String element) {
        this.values.add(index, element);
    }

    @Override
    public String remove(int index) {
        return this.values.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.values.lastIndexOf(o);
    }

    @Override
    public ListIterator<String> listIterator() {
        return this.values.listIterator();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return this.values.listIterator(index);
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        return this.values.subList(fromIndex, toIndex);
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
