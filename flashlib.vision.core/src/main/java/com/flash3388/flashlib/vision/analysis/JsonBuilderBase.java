package com.flash3388.flashlib.vision.analysis;

import com.google.gson.JsonObject;

public class JsonBuilderBase<T extends JsonBuilderBase<T>> {

    protected final JsonObject mProperties;
    private final Class<T> mDerivedType;

    protected JsonBuilderBase(Class<T> derivedType) {
        mDerivedType = derivedType;
        mProperties = new JsonObject();
    }

    public T put(String name, String value) {
        mProperties.addProperty(name, value);
        return mDerivedType.cast(this);
    }

    public T put(String name, int value) {
        mProperties.addProperty(name, value);
        return mDerivedType.cast(this);
    }

    public T put(String name, double value) {
        mProperties.addProperty(name, value);
        return mDerivedType.cast(this);
    }

    public T put(String name, boolean value) {
        mProperties.addProperty(name, value);
        return mDerivedType.cast(this);
    }
}
