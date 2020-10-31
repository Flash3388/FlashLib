package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.VisionResult;

import java.util.Optional;
import java.util.function.Consumer;

public interface VisionControl {

    boolean isRunning();

    void start();
    void stop();

    <T> void setOption(VisionOption<T> option, T value);

    Optional<VisionResult> getLatestResult();
    Optional<VisionResult> getLatestResult(boolean clear);
    Optional<VisionResult> getLatestResult(Time maxTimestamp);
    Optional<VisionResult> getLatestResult(Time maxTimestamp, boolean clear);

    void addResultListener(Consumer<VisionResult> consumer);
}
