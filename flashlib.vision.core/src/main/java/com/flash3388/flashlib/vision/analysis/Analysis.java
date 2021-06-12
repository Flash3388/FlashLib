package com.flash3388.flashlib.vision.analysis;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public interface Analysis {

    static JsonAnalysis.Builder builder() {
        return new JsonAnalysis.Builder();
    }

    List<? extends Target> getDetectedTargets();

    boolean hasProperty(String name);
    <T> T getProperty(String name, Class<T> type);

    void serializeTo(DataOutput dataOutput) throws IOException;
}
