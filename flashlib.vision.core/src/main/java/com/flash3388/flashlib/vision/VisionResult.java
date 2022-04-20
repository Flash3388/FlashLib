package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.analysis.Analysis;

public class VisionResult {

    private final Analysis mAnalysis;
    private final Time mTimestamp;

    public VisionResult(Analysis analysis, Time timestamp) {
        mAnalysis = analysis;
        mTimestamp = timestamp;
    }

    public Analysis getAnalysis() {
        return mAnalysis;
    }

    public Time getTimestamp() {
        return mTimestamp;
    }
}
