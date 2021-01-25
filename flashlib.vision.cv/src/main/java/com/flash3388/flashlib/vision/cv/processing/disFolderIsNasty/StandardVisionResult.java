package com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;

public class StandardVisionResult {
    private final Time timestamp;
    private final double distance;
    private final double angle;

    public StandardVisionResult(Time timestamp, double distance, double angle) {
        this.timestamp = timestamp;
        this.distance = distance;
        this.angle = angle;
    }

    public static StandardVisionResult fromAnalysis(Analysis analysis) {
        return new StandardVisionResult((Time)analysis.get("timestamp"), (double)analysis.get("distance"), (double)analysis.get("angle"));
    }
}
