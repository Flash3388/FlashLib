package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.VisionResult;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.control.event.VisionListener;
import com.notifier.EventController;

import java.util.Optional;

public class BasicVisionControl implements VisionControl {

    private final VisionBackend mBackend;
    private final EventController mEventController;
    private final VisionControlData mData;

    public BasicVisionControl(VisionBackend backend, EventController eventController, Clock clock) {
        mBackend = backend;
        mEventController = eventController;
        mData = new VisionControlData(eventController, clock);

        mBackend.setListener(new BackendListener(mData));
    }

    @Override
    public boolean isRunning() {
        return mData.isRunning();
    }

    @Override
    public void start() {
        verifyNotRunning();
        mBackend.start();
    }

    @Override
    public void stop() {
        verifyRunning();
        mBackend.stop();
    }

    @Override
    public <T> void setOption(VisionOption<T> option, T value) {
        verifyRunning();
        mData.setOption(option, value);
    }

    @Override
    public <T> Optional<T> getOption(VisionOption<T> option) {
        verifyRunning();
        return mData.getOption(option);
    }

    @Override
    public <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue) {
        verifyRunning();
        return mData.getOptionOrDefault(option, defaultValue);
    }

    @Override
    public Optional<VisionResult> getLatestResult() {
        verifyRunning();
        return mData.getLatestResult();
    }

    @Override
    public Optional<VisionResult> getLatestResult(boolean clear) {
        verifyRunning();
        return mData.getLatestResult(clear);
    }

    @Override
    public Optional<VisionResult> getLatestResult(Time maxTimestamp) {
        verifyRunning();
        return mData.getLatestResult(maxTimestamp);
    }

    @Override
    public Optional<VisionResult> getLatestResult(Time maxTimestamp, boolean clear) {
        verifyRunning();
        return mData.getLatestResult(maxTimestamp, clear);
    }

    @Override
    public void addListener(VisionListener listener) {
        mEventController.registerListener(listener);
    }

    private void verifyRunning() {
        if (!isRunning()) {
            throw new IllegalArgumentException("not running");
        }
    }

    private void verifyNotRunning() {
        if (!isRunning()) {
            throw new IllegalArgumentException("running");
        }
    }

    private static class BackendListener implements VisionBackend.Listener {

        private final VisionControlData mData;

        private BackendListener(VisionControlData data) {
            mData = data;
        }

        @Override
        public void onStarted() {
            mData.setRunning(true);
        }

        @Override
        public void onStopped() {
            mData.setRunning(false);
        }

        @Override
        public <T> void onOptionChanged(VisionOption<T> option, T value) {
            mData.setOption(option, value);
        }

        @Override
        public void onNewAnalysis(Analysis analysis) {
            mData.newAnalysis(analysis);
        }
    }
}
