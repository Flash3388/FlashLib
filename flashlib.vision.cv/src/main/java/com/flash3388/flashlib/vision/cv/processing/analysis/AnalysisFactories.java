package com.flash3388.flashlib.vision.cv.processing.analysis;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.AnalysisAlgorithms;

import java.util.function.BiFunction;

public class AnalysisFactories {

    private AnalysisFactories() {}

    public static BiFunction<? super Image, ? super Scorable, ? extends Analysis> positioningAnalysis(double camFovRadians,
                                                                                                      double targetRealWidth) {
        return (image, scorable) -> new Analysis.Builder()
                .put("distance", AnalysisAlgorithms
                        .measureDistance(scorable.getWidth(), image.getWidth(), targetRealWidth, camFovRadians))
                .put("angle", AnalysisAlgorithms
                        .calculateHorizontalOffsetDegrees2(scorable.getCenter().x(), image.getWidth(), camFovRadians))
                .build();
    }
}
