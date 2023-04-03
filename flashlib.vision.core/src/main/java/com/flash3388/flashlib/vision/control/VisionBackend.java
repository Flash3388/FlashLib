package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.vision.analysis.Analysis;

public interface VisionBackend extends AutoCloseable {

    interface Listener {
        void onStarted();
        void onStopped();

        <T> void onOptionChanged(VisionOption<T> option, T value);
        void onNewAnalysis(Analysis analysis);
    }

    void setListener(Listener listener);

    void start();
    void stop();

    <T> void setOption(VisionOption<T> option, T value);
}
