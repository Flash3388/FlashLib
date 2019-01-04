package edu.flash3388.flashlib.time;

import edu.flash3388.flashlib.util.CompareResult;

import java.util.concurrent.TimeUnit;

public class Time implements Comparable<Time> {

    public static final long INVALID_VALUE = -1L;
    public static final Time INVALID = new Time(INVALID_VALUE, TimeUnit.MILLISECONDS);

    private final long mValue;
    private final TimeUnit mUnit;

    public Time(long value, TimeUnit unit) {
        mValue = value;
        mUnit = unit;
    }

    public static Time milliseconds(long timeMs) {
        return new Time(timeMs, TimeUnit.MILLISECONDS);
    }

    public static Time seconds(long timeSeconds) {
        return new Time(timeSeconds, TimeUnit.SECONDS);
    }

    public long getValue() {
        return mValue;
    }

    public TimeUnit getUnit() {
        return mUnit;
    }

    public Time getAsUnit(TimeUnit newTimeUnit) {
        if (!isValid()) {
            return new Time(INVALID_VALUE, newTimeUnit);
        }

        long valueInWantedUnits = newTimeUnit.convert(mValue, mUnit);
        return new Time(valueInWantedUnits, newTimeUnit);
    }

    public long getAsMillis() {
        return getAsUnit(TimeUnit.MILLISECONDS).getValue();
    }

    public boolean isValid() {
        return mValue >= 0;
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
