package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.time.Time;

public interface Control {

    Time getStartTime();
    boolean wasInterrupted();

    void finished();
}
