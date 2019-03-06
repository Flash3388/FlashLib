package com.flash3388.flashlib.util.concurrent;

import com.flash3388.flashlib.math.Mathf;
import com.flash3388.flashlib.time.Time;

import java.util.function.BooleanSupplier;

public class Sleeper {

    private static final long MIN_SLEEP_PERIOD_MS = 5;
    private static final long MAX_SLEEP_PERIOD_MS = 1000;

    public void sleepMs(long sleepTimeMs) throws InterruptedException {
        Thread.sleep(sleepTimeMs);
    }

    public void sleep(Time sleepTime) throws InterruptedException {
        sleepMs(sleepTime.getAsMillis());
    }

    public void sleepWhileConditionMet(BooleanSupplier condition, Time sleepTime) throws InterruptedException {
        long timeoutMs = sleepTime.getAsMillis();
        long sleepingPeriodMs = getSleepingPeriodMs(timeoutMs);
        long timeWaited = 0;

        while (condition.getAsBoolean() && timeWaited < timeoutMs) {
            sleepMs(sleepingPeriodMs);
            timeWaited += sleepingPeriodMs;
        }
    }

    private long getSleepingPeriodMs(long timeoutMs) {
        if (timeoutMs <= MIN_SLEEP_PERIOD_MS) {
            return MIN_SLEEP_PERIOD_MS;
        }

        return (long) Mathf.scale(timeoutMs, MIN_SLEEP_PERIOD_MS, MAX_SLEEP_PERIOD_MS);
    }
}
