package com.flash3388.flashlib.hmi.impl;

@FunctionalInterface
public interface ChangeListener {

    void valueChanged(Object newValue);
}
