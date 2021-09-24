package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.time.Time;

public interface Status<R> {

    boolean isPending();
    boolean isDone();
    boolean isSuccessful();
    boolean isErrored();
    boolean isCanceled();

    Time getStartTime();

    R getResult();
    Throwable getError();

    void cancel();

    // inner

    Time getQueuedTime();

    void markStarted(Time time);
    void markFinished(R result);
    void markErrored(Throwable error);
}
