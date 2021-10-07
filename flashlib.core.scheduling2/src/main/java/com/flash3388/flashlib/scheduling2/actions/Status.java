package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.time.Time;

public interface Status {

    boolean isPending();
    boolean isDone();
    boolean isSuccessful();
    boolean isErrored();
    boolean isCanceled();

    Time getStartTime();

    Throwable getError();

    void cancel();

    // inner

    void markStarted(Time time);
    void markFinished();
    void markErrored(Throwable error);
}
