package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.time.Time;

public interface Control<R> {

    Time getStartTime();

    boolean wasInterrupted();

    void finished();
    void finished(R result);


    // inner

    boolean isFinished();

    void markInterrupted();

    R getResult();
}
