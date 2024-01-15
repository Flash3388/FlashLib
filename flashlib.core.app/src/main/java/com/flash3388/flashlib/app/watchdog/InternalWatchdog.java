package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Time;

public interface InternalWatchdog extends Watchdog {

    Time getTimeLeftToTimeout();

    void checkFed();
}
