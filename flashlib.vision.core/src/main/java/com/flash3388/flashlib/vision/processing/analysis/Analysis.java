package com.flash3388.flashlib.vision.processing.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Analysis {

    public static class Builder {

        private final Map<String, Object> mData;

        public Builder(Map<String, Object> data) {
            mData = data;
        }

        public Builder() {
            this(new HashMap<>());
        }

        public Builder put(String key, Object value) {
            mData.put(key, value);
            return this;
        }

        public Analysis build() {
            return new Analysis(mData);
        }
    }

    private final Map<String, Object> mData;

    public Analysis(Map<String, Object> data) {
        Objects.requireNonNull(data, "data is null");
        mData = Collections.unmodifiableMap(new HashMap<>(data));
    }

    public Map<String, Object> getData() {
        return mData;
    }

    @Override
    public String toString() {
        return mData.toString();
    }
}
