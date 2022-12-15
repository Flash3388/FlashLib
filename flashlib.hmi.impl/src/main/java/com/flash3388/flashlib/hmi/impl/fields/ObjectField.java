package com.flash3388.flashlib.hmi.impl.fields;

import com.flash3388.flashlib.hmi.impl.ChangeListener;

public interface ObjectField {

    Object get();
    void set(Object value);

    void addChangeListener(ChangeListener listener);
}
