package edu.flash3388.flashlib.util.concurrent;

import edu.flash3388.flashlib.math.Mathf;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class Sleeper {

    private static final long MIN_SLEEP_PERIOD_MS = 5;
    private static final long MAX_SLEEP_PERIOD_MS = 1000;

    public void sleep(long sleepTime, TimeUnit timeUnit) throws InterruptedException {
        Thread.sleep(timeUnit.toMillis(sleepTime));
    }

    public void sleepWhileConditionMet(BooleanSupplier condition, long timeout, TimeUnit timeUnit) throws InterruptedException {
        long timeoutMs = timeUnit.toMillis(timeout);
        long sleepingPeriodMs = getSleepingPeriodMs(timeoutMs);
        long timeWaited = 0;

        while (condition.getAsBoolean() && timeWaited < timeoutMs) {
            sleep(sleepingPeriodMs, TimeUnit.MILLISECONDS);
            timeWaited += sleepingPeriodMs;
        }
    }

    private long getSleepingPeriodMs(long timeoutMs) {
        if (timeoutMs < MIN_SLEEP_PERIOD_MS) {
            return MIN_SLEEP_PERIOD_MS;
        }

        return (long) Mathf.scale(timeoutMs, MIN_SLEEP_PERIOD_MS, MAX_SLEEP_PERIOD_MS);
    }
}
