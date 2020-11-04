package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.VisionResult;
import com.flash3388.flashlib.vision.control.event.VisionListener;

import java.util.Optional;

public interface VisionControl {

    boolean isRunning();

    void start();
    void stop();

    <T> void setOption(VisionOption<T> option, T value);
    <T> Optional<T> getOption(VisionOption<T> option);
    <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue);

    Optional<VisionResult> getLatestResult();
    Optional<VisionResult> getLatestResult(boolean clear);
    Optional<VisionResult> getLatestResult(Time maxTimestamp);
    Optional<VisionResult> getLatestResult(Time maxTimestamp, boolean clear);

    void addListener(VisionListener listener);
}
