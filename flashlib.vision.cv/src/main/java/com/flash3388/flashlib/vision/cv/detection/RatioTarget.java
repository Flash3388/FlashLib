package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.detection.RealTargetConfig;
import com.flash3388.flashlib.vision.detection.ScorableTarget;
import com.jmath.vectors.Vector2;
import org.opencv.core.Rect;

public class RatioTarget implements ScorableTarget {

    private final Rect mRect;
    private final RealTargetConfig mRealTargetConfig;

    public RatioTarget(Rect rect, RealTargetConfig realTargetConfig) {
        mRect = rect;
        mRealTargetConfig = realTargetConfig;
    }

    @Override
    public int getWidthPixels() {
        return mRect.width;
    }

    @Override
    public int getHeightPixels() {
        return mRect.height;
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(
                mRect.x + mRect.width * 0.5,
                mRect.y + mRect.height * 0.5
        );
    }

    @Override
    public double score() {
        double ratio = mRect.width / (double) mRect.height;
        return ratio > mRealTargetConfig.getDimensionsRatio() ?
                mRealTargetConfig.getDimensionsRatio() / ratio :
                ratio / mRealTargetConfig.getDimensionsRatio();
    }
}
