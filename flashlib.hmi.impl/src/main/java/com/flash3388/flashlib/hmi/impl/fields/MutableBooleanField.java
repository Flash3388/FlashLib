package com.flash3388.flashlib.hmi.impl.fields;

import com.beans.observables.properties.ObservableBooleanProperty;
import com.flash3388.flashlib.hmi.impl.ChangeListener;

public class MutableBooleanField implements ObjectField {

    private final ObservableBooleanProperty mProperty;

    public MutableBooleanField(ObservableBooleanProperty property) {
        mProperty = property;
    }

    @Override
    public Object get() {
        return mProperty.get();
    }

    @Override
    public void set(Object value) {
        if (!Boolean.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("expected Boolean");
        }

        mProperty.setAsBoolean((Boolean) value);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        mProperty.addChangeListener((e)-> {
            listener.valueChanged(e.getNewValue());
        });
    }
}
