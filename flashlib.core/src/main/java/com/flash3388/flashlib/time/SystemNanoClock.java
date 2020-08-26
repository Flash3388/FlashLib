package com.flash3388.flashlib.time;

import java.util.concurrent.TimeUnit;

public class SystemNanoClock implements Clock {

    private final long mStartTimeNanos;

    public SystemNanoClock() {
        mStartTimeNanos = System.nanoTime();
    }

    @Override
    public Time currentTime() {
        long timeNanos = System.nanoTime() - mStartTimeNanos;
        return new Time(timeNanos, TimeUnit.NANOSECONDS);
    }
}
