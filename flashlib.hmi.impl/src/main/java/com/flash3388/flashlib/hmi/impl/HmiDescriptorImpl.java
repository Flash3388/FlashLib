package com.flash3388.flashlib.hmi.impl;

import com.beans.observables.properties.ObservableBooleanProperty;
import com.beans.observables.properties.ObservableDoubleProperty;
import com.beans.observables.properties.ObservableIntProperty;
import com.beans.observables.properties.ObservableProperty;
import com.flash3388.flashlib.hmi.HmiDescriptor;
import com.flash3388.flashlib.hmi.HmiObject;
import com.flash3388.flashlib.hmi.impl.fields.MutableBooleanField;
import com.flash3388.flashlib.hmi.impl.fields.ObjectField;

import java.util.HashMap;
import java.util.Map;

public class HmiDescriptorImpl implements HmiDescriptor {

    private final Map<String, ObjectField> mFields;

    public HmiDescriptorImpl() {
        mFields = new HashMap<>();
    }

    public Map<String, ObjectField> getFields() {
        return mFields;
    }

    @Override
    public void registerField(String name, ObservableBooleanProperty property) {
        mFields.put(name, new MutableBooleanField(property));
    }

    @Override
    public void registerField(String name, ObservableIntProperty property) {

    }

    @Override
    public void registerField(String name, ObservableDoubleProperty property) {

    }

    @Override
    public void registerField(String name, ObservableProperty<String> property) {

    }

    @Override
    public void registerObject(String name, HmiObject object) {

    }
}
