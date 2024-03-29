package com.flash3388.flashlib.vision.analysis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonAnalysisParser {

    public JsonObject parseProperties(JsonObject root) {
        if (root.has("properties")) {
            JsonElement element = root.get("properties");
            if (!element.isJsonObject()) {
                throw new IllegalArgumentException("Unexpected format: 'properties' should be object");
            }

            return element.getAsJsonObject();
        } else {
            return new JsonObject();
        }
    }

    public List<JsonTarget> parseTargets(JsonObject root) {
        if (root.has("targets")) {
            JsonElement element = root.get("targets");
            if (!element.isJsonArray()) {
                throw new IllegalArgumentException("Unexpected format: 'targets' should be array");
            }

            JsonArray array = element.getAsJsonArray();
            return parseTargets(array);
        }

        return Collections.emptyList();
    }

    private List<JsonTarget> parseTargets(JsonArray array) {
        List<JsonTarget> targets = new ArrayList<>();
        for (JsonElement element : array) {
            JsonTarget target = parseTarget(element);
            targets.add(target);
        }

        return targets;
    }

    private JsonTarget parseTarget(JsonElement element) {
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Unexpected format: target def should be object");
        }

        JsonObject object = element.getAsJsonObject();
        return new JsonTarget(object);
    }
}
