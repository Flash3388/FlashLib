package com.flash3388.flashlib.hmi;

import java.util.Arrays;
import java.util.Iterator;

public class Namespace implements Iterable<String> {

    private final String[] mNameParts;

    public Namespace(String namespace) {
        mNameParts = namespace.split("\\.");
    }

    public int length() {
        return mNameParts.length;
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(mNameParts).iterator();
    }
}
