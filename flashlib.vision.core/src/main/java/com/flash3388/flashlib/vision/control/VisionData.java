package com.flash3388.flashlib.vision.control;

import java.util.Optional;

public class VisionData<T> {

    private final T mData;
    private final VisionOptions mVisionOptions;

    public VisionData(T data, VisionOptions visionOptions) {
        mData = data;
        mVisionOptions = visionOptions;
    }

    public T getData() {
        return mData;
    }

    public <OT> Optional<OT> getOption(VisionOption option, Class<OT> type) {
        return mVisionOptions.get(option, type);
    }

    public <OT> OT getOptionOrDefault(VisionOption option, Class<OT> type, OT defaultValue) {
        return mVisionOptions.getOrDefault(option, type, defaultValue);
    }
}
