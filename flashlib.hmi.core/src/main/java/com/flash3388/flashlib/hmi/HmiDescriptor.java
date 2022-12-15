package com.flash3388.flashlib.hmi;

import com.beans.observables.properties.ObservableBooleanProperty;
import com.beans.observables.properties.ObservableDoubleProperty;
import com.beans.observables.properties.ObservableIntProperty;
import com.beans.observables.properties.ObservableProperty;

public interface HmiDescriptor {

    void registerField(String name, ObservableBooleanProperty property);
    void registerField(String name, ObservableIntProperty property);
    void registerField(String name, ObservableDoubleProperty property);
    void registerField(String name, ObservableProperty<String> property);

    void registerObject(String name, HmiObject object);
}
