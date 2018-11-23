package edu.flash3388.flashlib.time;

import edu.flash3388.flashlib.util.CompareResult;

import java.util.concurrent.TimeUnit;

public class Time implements Comparable<Time> {

    public static final long INVALID_TIME_VALUE = -1L;
    public static final Time INVALID_TIME = new Time(INVALID_TIME_VALUE, TimeUnit.MILLISECONDS);

    private final long mTimeValue;
    private final TimeUnit mTimeUnit;

    public Time(long timeValue, TimeUnit timeUnit) {
        mTimeValue = timeValue;
        mTimeUnit = timeUnit;
    }

    public static Time forMillis(long timeMs) {
        return new Time(timeMs, TimeUnit.MILLISECONDS);
    }

    public long getTimeValue() {
        return mTimeValue;
    }

    public Time getForUnit(TimeUnit newTimeUnit) {
        if (!isValid()) {
            return new Time(INVALID_TIME_VALUE, newTimeUnit);
        }

        long valueInWantedUnits = newTimeUnit.convert(mTimeValue, mTimeUnit);
        return new Time(valueInWantedUnits, newTimeUnit);
    }

    public long getAsMillis() {
        return getForUnit(TimeUnit.MILLISECONDS).getTimeValue();
    }

    public boolean isValid() {
        return mTimeValue >= 0;
    }

    public Time add(Time other) {
        long thisMs = getAsMillis();
        long otherMs = other.getAsMillis();

        return new Time(thisMs + otherMs, TimeUnit.MILLISECONDS);
    }

    public Time sub(Time other) {
        long thisMs = getAsMillis();
        long otherMs = other.getAsMillis();

        return new Time(thisMs - otherMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Time other) {
        long thisMs = getAsMillis();
        long otherMs = other.getAsMillis();

        if (thisMs > otherMs) {
            return CompareResult.GREATER_THAN.getValue();
        }
        if (thisMs < otherMs) {
            return CompareResult.SMALLER_THAN.getValue();
        }

        return CompareResult.EQUAL_TO.getValue();
    }
}
