package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.detection.RealTargetConfig;
import com.flash3388.flashlib.vision.detection.ScorableTarget;
import org.opencv.core.Rect;

public class RatioTarget extends RectTarget implements ScorableTarget {
    private final RealTargetConfig mRealTargetConfig;

    public RatioTarget(Rect rect, RealTargetConfig realTargetConfig) {
        super(rect);
        mRealTargetConfig = realTargetConfig;
    }

    @Override
    public double score() {
        double ratio = rect.width / (double) rect.height;
        return ratio > mRealTargetConfig.getDimensionsRatio() ?
                mRealTargetConfig.getDimensionsRatio() / ratio :
                ratio / mRealTargetConfig.getDimensionsRatio();
    }
}
