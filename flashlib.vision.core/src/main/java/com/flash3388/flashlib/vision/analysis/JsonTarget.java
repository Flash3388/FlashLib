package com.flash3388.flashlib.vision.analysis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.NoSuchElementException;
import java.util.Objects;

public class JsonTarget implements Target {

    public static class Builder extends JsonBuilderBase<Builder> {

        private final Gson mGson;

        private Builder(Gson gson) {
            super(Builder.class);
            mGson = gson;
        }

        Builder() {
            this(new Gson());
        }

        public JsonTarget build() {
            return new JsonTarget(mGson, mProperties);
        }
    }

    private final Gson mGson;
    private final JsonObject mProperties;

    public JsonTarget(Gson gson, JsonObject properties) {
        mGson = gson;
        mProperties = properties;
    }

    public JsonTarget(JsonObject properties) {
        this(new Gson(), properties);
    }

    @Override
    public boolean hasProperty(String name) {
        return mProperties.has(name);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        JsonElement element = mProperties.get(name);
        if (element == null) {
            throw new NoSuchElementException(name);
        }

        return mGson.fromJson(element, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonTarget that = (JsonTarget) o;
        return Objects.equals(mProperties, that.mProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mProperties);
    }

    public JsonObject toJson() {
        return mProperties.deepCopy();
    }
}
