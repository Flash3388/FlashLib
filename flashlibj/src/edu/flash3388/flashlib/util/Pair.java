package edu.flash3388.flashlib.util;

public class Pair<K, V> {

    private K mKey;
    private V mValue;

    public Pair(K key, V value) {
        mKey = key;
        mValue = value;
    }

    public K getKey() {
        return mKey;
    }

    public V getValue() {
        return mValue;
    }

    public static <K, V> Pair<K, V> create(K key, V value) {
        return new Pair<K, V>(key, value);
    }
}
