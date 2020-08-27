package com.flash3388.flashlib.time;

import java.util.concurrent.TimeUnit;

/**
 * A {@link Clock} based on <b>Java's</b> {@link System#nanoTime()}.
 *
 * @since FlashLib 2.0.0
 */
public class SystemNanoClock implements Clock {

    private final long mStartTimeNanos;

    public SystemNanoClock() {
        mStartTimeNanos = System.nanoTime();
    }

    /**
     * {@inheritDoc}
     * <p>
     *     The returned timestamp is a time difference between the current time and the time at which the
     *     current instance was created.
     * </p>
     */
    @Override
    public Time currentTime() {
        long timeNanos = System.nanoTime() - mStartTimeNanos;
        return new Time(timeNanos, TimeUnit.NANOSECONDS);
    }
}
