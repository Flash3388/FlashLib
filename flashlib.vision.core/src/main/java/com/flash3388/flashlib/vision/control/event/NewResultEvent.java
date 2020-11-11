package com.flash3388.flashlib.vision.control.event;

import com.flash3388.flashlib.vision.VisionResult;
import com.notifier.Event;

public class NewResultEvent implements Event {

    private final VisionResult mVisionResult;

    public NewResultEvent(VisionResult visionResult) {
        mVisionResult = visionResult;
    }

    public VisionResult getVisionResult() {
        return mVisionResult;
    }
}
