package edu.flash3388.flashlib.util;

public class Pair<K, V> {

    private final K mFirst;
    private final V mSecond;

    public Pair(K first, V second) {
        mFirst = first;
        mSecond = second;
    }

    public K getFirst() {
        return mFirst;
    }

    public V getSecond() {
        return mSecond;
    }

    public static <K, V> Pair<K, V> create(K key, V value) {
        return new Pair<K, V>(key, value);
    }
}
