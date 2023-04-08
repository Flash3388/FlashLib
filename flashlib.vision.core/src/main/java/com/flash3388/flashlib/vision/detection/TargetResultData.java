package com.flash3388.flashlib.vision.detection;

import com.flash3388.flashlib.vision.Image;

import java.util.Map;

public class TargetResultData<I extends Image, T extends Target> {

    private final I mImage;
    private final Map<Integer, ? extends T> mTargets;

    public TargetResultData(I image, Map<Integer, ? extends T> targets) {
        mImage = image;
        mTargets = targets;
    }

    public I getImage() {
        return mImage;
    }

    public Map<Integer, ? extends T> getTargets() {
        return mTargets;
    }
}
