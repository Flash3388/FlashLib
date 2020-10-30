package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.VisionResult;

import java.util.Optional;

public interface VisionControl {

    boolean isRunning();

    void start();
    void stop();

    Optional<VisionResult> getLatestResult();
    Optional<VisionResult> getLatestResult(Time maxTimestamp);

    <T> void setOption(VisionOption<T> option, T value);
}
