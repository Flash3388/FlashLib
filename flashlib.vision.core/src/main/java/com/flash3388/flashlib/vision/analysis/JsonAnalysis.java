package com.flash3388.flashlib.vision.analysis;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class JsonAnalysis implements Analysis {

    public static class Builder {

        private final Gson mGson;
        private final List<JsonTarget> mTargets;
        private final JsonObject mProperties;

        public Builder(Gson gson) {
            mGson = gson;
            mTargets = new ArrayList<>();
            mProperties = new JsonObject();
        }

        public Builder() {
            this(new Gson());
        }

        public Builder addTarget(JsonTarget target) {
            mTargets.add(target);
            return this;
        }

        public TargetBuilder buildTarget() {
            return new TargetBuilder(this, mGson);
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
            return new JsonAnalysis(mGson, mTargets, mProperties);
        }
    }

    public static class TargetBuilder {

        private final Builder mBuilder;
        private final Gson mGson;
        private final JsonObject mProperties;

        private TargetBuilder(Builder builder, Gson gson) {
            mBuilder = builder;
            mGson = gson;
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
            JsonTarget target = new JsonTarget(mGson, mProperties);
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

    public JsonAnalysis(DataInput dataInput) throws IOException {
        try {
            String rawJson = dataInput.readUTF();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(rawJson);
            if (!element.isJsonObject()) {
                throw new IOException("Expected root to be json object");
            }

            JsonObject root = element.getAsJsonObject();

            JsonAnalysisParser analysisParser = new JsonAnalysisParser();
            mGson = new Gson();
            mTargets = Collections.unmodifiableList(analysisParser.parseTargets(root));
            mProperties = analysisParser.parseProperties(root);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
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

    @Override
    public void serializeTo(DataOutput dataOutput) throws IOException {
        JsonObject root = toJson();
        dataOutput.writeUTF(mGson.toJson(root));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonAnalysis analysis = (JsonAnalysis) o;
        return Objects.equals(mTargets, analysis.mTargets) &&
                Objects.equals(mProperties, analysis.mProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTargets, mProperties);
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
