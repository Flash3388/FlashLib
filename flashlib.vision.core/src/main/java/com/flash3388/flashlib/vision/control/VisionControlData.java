package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.VisionResult;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.control.event.NewResultEvent;
import com.flash3388.flashlib.vision.control.event.VisionListener;
import com.notifier.EventController;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VisionControlData {

    private final EventController mEventController;
    private final Clock mClock;

    private final VisionOptions mVisionOptions;
    private final AtomicReference<VisionResult> mLatestResult;
    private final AtomicBoolean mIsRunning;

    public VisionControlData(EventController eventController, Clock clock) {
        mEventController = eventController;
        mClock = clock;

        mVisionOptions = new VisionOptions();
        mLatestResult = new AtomicReference<>();
        mIsRunning = new AtomicBoolean(false);
    }

    public boolean isRunning() {
        return mIsRunning.get();
    }

    public void setRunning(boolean running) {
        mIsRunning.set(running);
    }

    public <T> void setOption(VisionOption<T> option, T value) {
        mVisionOptions.put(option, value);
    }

    public <T> Optional<T> getOption(VisionOption<T> option) {
        return mVisionOptions.get(option);
    }

    public <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue) {
        return mVisionOptions.getOrDefault(option, defaultValue);
    }

    public Optional<VisionResult> getLatestResult() {
        return getLatestResult(false);
    }

    public Optional<VisionResult> getLatestResult(boolean clear) {
        return Optional.ofNullable(clear ? mLatestResult.getAndSet(null) : mLatestResult.get());
    }

    public Optional<VisionResult> getLatestResult(Time maxTimestamp) {
        return getLatestResult(maxTimestamp, false);
    }

    public Optional<VisionResult> getLatestResult(Time maxTimestamp, boolean clear) {
        VisionResult result = clear ? mLatestResult.getAndSet(null) : mLatestResult.get();
        if (result == null) {
            return Optional.empty();
        }

        Time now = mClock.currentTime();
        Time passed = now.sub(result.getTimestamp());
        if (passed.after(maxTimestamp)) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    public void newAnalysis(Analysis analysis) {
        Time now = mClock.currentTime();
        VisionResult visionResult = new VisionResult(analysis, now);
        mLatestResult.set(visionResult);

        mEventController.fire(
                new NewResultEvent(visionResult),
                NewResultEvent.class,
                VisionListener.class,
                VisionListener::onNewResult);
    }
}
