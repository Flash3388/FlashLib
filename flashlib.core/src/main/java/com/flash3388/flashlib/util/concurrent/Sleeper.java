package com.flash3388.flashlib.util.concurrent;

import com.flash3388.flashlib.math.Mathf;
import com.flash3388.flashlib.time.Time;

import java.util.function.BooleanSupplier;

/**
 * Utility class for sleeping operations.
 *
 * @since FlashLib 2.0.0
 */
public class Sleeper {

    static final long MIN_SLEEP_PERIOD_MS = 5;
    private static final long MAX_SLEEP_PERIOD_MS = 1000;

    /**
     * Puts the current thread to sleep for the given amount of time.
     *
     * @param duration duration of sleep.
     *
     * @throws InterruptedException if the current thread is interrupted.
     *
     * @see Thread#sleep(long)
     */
    public void sleep(Time duration) throws InterruptedException {
        sleepMs(duration.valueAsMillis());
    }

    /**
     * Puts the current to thread to sleep for a given amount of time, or until the given condition is no
     * longer <b>true</b>.
     * <p>
     *     The actual sleeping is not continuous, but rather broken up into section. This allows
     *     to sample that condition between each sleep section. This also means that the sleeping
     *     is not entirely accurate.
     * </p>
     * <p>
     *     For more accurate waking up and sleeping, it is best to used a solution based on locks,
     *     or utilize java's {@link java.util.concurrent.ExecutorService} and {@link java.util.concurrent.Future}
     *     API's (depending on the need).
     * </p>
     *
     * @param condition condition to listen to.
     * @param duration duration of sleep.
     *
     * @throws InterruptedException if the current thread is interrupted.
     *
     * @see Thread#sleep(long)
     */
    public void sleepWhileConditionMet(BooleanSupplier condition, Time duration) throws InterruptedException {
        long timeoutMs = duration.valueAsMillis();
        long sleepingPeriodMs = getSleepingPeriodMs(timeoutMs);
        long timeWaited = 0;

        while (condition.getAsBoolean() && timeWaited < timeoutMs) {
            sleepMs(sleepingPeriodMs);
            timeWaited += sleepingPeriodMs;
        }
    }

    void sleepMs(long sleepTimeMs) throws InterruptedException {
        Thread.sleep(sleepTimeMs);
    }

    private long getSleepingPeriodMs(long timeoutMs) {
        timeoutMs = (long) Mathf.scale(timeoutMs, MIN_SLEEP_PERIOD_MS, MAX_SLEEP_PERIOD_MS);
        return Math.max(timeoutMs, MIN_SLEEP_PERIOD_MS);
    }
}
