package edu.flash3388.flashlib.vision.processing.analysis;

import org.json.JSONObject;

public class Analysis {

    private final JSONObject mData;

    public Analysis(JSONObject data) {
        mData = data;
    }

    public JSONObject getData() {
        return mData;
    }
}
