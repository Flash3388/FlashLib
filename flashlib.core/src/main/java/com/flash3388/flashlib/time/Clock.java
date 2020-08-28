package com.flash3388.flashlib.time;

/**
 * Represents a time-measuring object, capable of supplying the current time (by the clock's measurements).
 *
 * @since FlashLib 2.0.0
 */
public interface Clock {

    /**
     * Gets the current time. This doesn't necessarily represent any real world time,
     * but rather a timestamp. It's meaning depends on the implementation.
     * <p>At any given time, this code conforms to:</p>
     * <pre>
     *     Time time = clock.currentTime();
     *     Time time2 = clock.currentTime();
     *
     *     assert time2.largerThanOrEquals(time);
     * </pre>
     * <p>
     *     Whether <code>time2</code> is actually larger than <code>time</code> depends on the precision
     *     of the clock.
     * </p>
     *
     * @return the current time, as measured by the clock.
     */
    Time currentTime();
}
