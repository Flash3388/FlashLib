package com.flash3388.flashlib.hmi.impl;

import com.flash3388.flashlib.hmi.HmiObject;
import com.flash3388.flashlib.hmi.impl.fields.ObjectField;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegisteredObject {

    private final HmiObject mObject;
    private final Map<String, ObjectField> mFields;

    public RegisteredObject(HmiObject object) {
        mObject = object;
        mFields = new HashMap<>();

        HmiDescriptorImpl descriptor = new HmiDescriptorImpl();
        object.onHmiRegistration(descriptor);
        mFields.putAll(descriptor.getFields());
    }

    public void set(Iterator<String> name, Object value) {
        String first = name.next();
        if (!name.hasNext() && mFields.containsKey(first)) {
            // should be a field
            ObjectField field = mFields.get(first);
            field.set(value);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
