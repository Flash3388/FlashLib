package com.flash3388.flashlib.net.obsr;

import com.beans.observables.ObservableBooleanValue;
import com.beans.observables.ObservableDoubleValue;
import com.beans.observables.ObservableIntValue;
import com.beans.observables.ObservableLongValue;
import com.beans.observables.properties.ObservableProperty;

public interface ValueProperty extends ObservableProperty<Value> {

    default ObservableBooleanValue asBoolean(boolean defaultValue) {
        return asBoolean((v)-> v.getBoolean(defaultValue));
    }

    default ObservableIntValue asInt(int defaultValue) {
        return asInt((v)-> v.getInt(defaultValue));
    }

    default ObservableLongValue asLong(long defaultValue) {
        return asLong((v)-> v.getLong(defaultValue));
    }

    default ObservableDoubleValue asDouble(double defaultValue) {
        return asDouble((v)-> v.getDouble(defaultValue));
    }
}
