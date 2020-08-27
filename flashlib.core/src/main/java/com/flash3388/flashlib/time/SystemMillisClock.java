package com.flash3388.flashlib.time;

import java.util.concurrent.TimeUnit;

/**
 * A {@link Clock} based on <b>Java's</b> {@link System#currentTimeMillis()}.
 *
 * @since FlashLib 2.0.0
 */
public class SystemMillisClock implements Clock {

    /**
     * {@inheritDoc}
     * <p>
     *     The returned timestamp is a time difference between the current time,
     *     and EPOCH (midnight, January 1, 1970 UTC).
     * </p>
     */
    @Override
    public Time currentTime() {
        return new Time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
