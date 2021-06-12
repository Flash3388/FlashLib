package com.flash3388.flashlib.vision.analysis;

public interface Target {

    boolean hasProperty(String name);
    <T> T getProperty(String name, Class<T> type);
}
