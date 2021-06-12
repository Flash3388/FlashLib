package com.flash3388.flashlib.vision.analysis;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class JsonAnalysis implements Analysis {

    public static class Builder {

        private final List<JsonTarget> mTargets;
        private final JsonObject mProperties;

        public Builder() {
            mTargets = new ArrayList<>();
            mProperties = new JsonObject();
        }

        public Builder addTarget(JsonTarget target) {
            mTargets.add(target);
            return this;
        }

        public TargetBuilder buildTarget() {
            return new TargetBuilder(this);
        }

        public Builder put(String name, String value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public Builder put(String name, int value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public Builder put(String name, double value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public Builder put(String name, boolean value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public JsonAnalysis build() {
            return new JsonAnalysis(mTargets, mProperties);
        }
    }

    public static class TargetBuilder {

        private final Builder mBuilder;
        private final JsonObject mProperties;

        private TargetBuilder(Builder builder) {
            mBuilder = builder;
            mProperties = new JsonObject();
        }

        public TargetBuilder put(String name, String value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public TargetBuilder put(String name, int value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public TargetBuilder put(String name, double value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public TargetBuilder put(String name, boolean value) {
            mProperties.addProperty(name, value);
            return this;
        }

        public Builder build() {
            JsonTarget target = new JsonTarget(mProperties);
            return mBuilder.addTarget(target);
        }
    }

    private final Gson mGson;
    private final List<JsonTarget> mTargets;
    private final JsonObject mProperties;

    public JsonAnalysis(Gson gson, List<JsonTarget> targets, JsonObject properties) {
        mGson = gson;
        mTargets = Collections.unmodifiableList(targets);
        mProperties = properties;
    }

    public JsonAnalysis(List<JsonTarget> targets, JsonObject properties) {
        this(new Gson(), targets, properties);
    }

    public static JsonAnalysis parse(JsonObject data) {
        return new JsonAnalysisParser().parse(data);
    }

    @Override
    public List<? extends Target> getDetectedTargets() {
        return mTargets;
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

    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("targets", targetsToJson());
        root.add("properties", mProperties);

        return root;
    }

    private JsonArray targetsToJson() {
        JsonArray array = new JsonArray();
        for (JsonTarget target : mTargets) {
            JsonObject object = target.toJson();
            array.add(object);
        }

        return array;
    }
}
