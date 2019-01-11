package edu.flash3388.flashlib.vision.processing.analysis;

import com.google.gson.JsonObject;

public class Analysis {

    private final JsonObject mData;

    public Analysis(JsonObject data) {
        mData = data;
    }

    public JsonObject getData() {
        return mData;
    }
}
