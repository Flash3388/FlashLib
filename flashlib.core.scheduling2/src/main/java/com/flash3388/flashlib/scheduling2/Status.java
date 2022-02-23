package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.time.Time;

public interface Status {

    boolean isPending();
    boolean isRunning();
    boolean isDone();
    boolean isSuccessful();
    boolean isErrored();
    boolean isCanceled();

    Time getStartTime();
    Throwable getError();

    void cancel();
}
