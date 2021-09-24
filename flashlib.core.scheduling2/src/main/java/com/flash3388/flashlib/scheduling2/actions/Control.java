package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.time.Time;

public interface Control {

    Time getStartTime();

    boolean wasInterrupted();

    void finished();

    ActionExecutionBuilder start(Action action);

    // inner

    boolean isFinished();

    void markInterrupted();
}
