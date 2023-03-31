package com.flash3388.flashlib.io.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GroupBuilder<E, T> {

    private final Function<List<E>, T> mCreator;
    private final List<E> mParts;

    public GroupBuilder(Function<List<E>, T> creator) {
        mCreator = creator;
        mParts = new ArrayList<>();
    }

    public GroupBuilder<E, T> add(E e) {
        mParts.add(e);
        return this;
    }

    public T build() {
        return mCreator.apply(mParts);
    }
}
