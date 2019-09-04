package com.flash3388.flashlib.time;

import com.flash3388.flashlib.util.CompareResult;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Time implements Comparable<Time> {

    public static final long INVALID_VALUE = -1L;
    public static final Time INVALID = new Time(INVALID_VALUE, TimeUnit.MILLISECONDS);

    private final long mValue;
    private final TimeUnit mUnit;

    public Time(long value, TimeUnit unit) {
        mValue = value;
        mUnit = Objects.requireNonNull(unit, "time unit");
    }

    public static Time of(long value, TimeUnit unit) {
        return new Time(value, unit);
    }

    public static Time milliseconds(long timeMs) {
        return new Time(timeMs, TimeUnit.MILLISECONDS);
    }

    public static Time seconds(long timeSeconds) {
        return new Time(timeSeconds, TimeUnit.SECONDS);
    }

    public static Time seconds(double timeSeconds) {
        return milliseconds((long) (timeSeconds * 1000));
    }

    public static Time minutes(long timeMinutes) {
        return new Time(timeMinutes, TimeUnit.MINUTES);
    }

    public static Time minutes(double timeMinutes) {
        return seconds(timeMinutes * 60);
    }

    public long value() {
        return mValue;
    }

    public TimeUnit unit() {
        return mUnit;
    }

    public Time toUnit(TimeUnit newTimeUnit) {
        if (!isValid()) {
            return new Time(INVALID_VALUE, newTimeUnit);
        }

        if (mUnit == newTimeUnit) {
            return this;
        }

        long valueInWantedUnits = newTimeUnit.convert(mValue, mUnit);
        return new Time(valueInWantedUnits, newTimeUnit);
    }

    public long valueAsMillis() {
        return toUnit(TimeUnit.MILLISECONDS).value();
    }

    public boolean isValid() {
        return mValue >= 0;
    }

    public Time add(Time other) {
        checkValidForOperation(other);

        long newValue = mValue + other.toUnit(mUnit).value();
        return new Time(newValue, mUnit);
    }

    public Time sub(Time other) {
        checkValidForOperation(other);

        TimeUnit smallerUnit = UnitComparing.smallerUnit(mUnit, other.unit());
        long newValue = smallerUnit.convert(mValue, mUnit) - other.toUnit(smallerUnit).value();
        return new Time(newValue, smallerUnit);
    }

    public boolean before(Time other) {
        return lessThan(other);
    }

    public boolean lessThan(Time other) {
        return CompareResult.SMALLER_THAN.is(compareTo(other));
    }

    public boolean lessThanOrEquals(Time other) {
        int compareResult = compareTo(other);
        return CompareResult.in(compareResult, CompareResult.SMALLER_THAN, CompareResult.EQUAL_TO);
    }

    public boolean after(Time other) {
        return largerThan(other);
    }

    public boolean largerThan(Time other) {
        return CompareResult.GREATER_THAN.is(compareTo(other));
    }

    public boolean largerThanOrEquals(Time other) {
        int compareResult = compareTo(other);
        return CompareResult.in(compareResult, CompareResult.GREATER_THAN, CompareResult.EQUAL_TO);
    }

    public boolean equals(Time other) {
        return CompareResult.EQUAL_TO.is(compareTo(other));
    }

    @Override
    public int compareTo(Time other) {
        if (!isValid() && !other.isValid()) {
            return CompareResult.EQUAL_TO.value();
        } if (!other.isValid()) {
            return CompareResult.GREATER_THAN.value();
        } if (!isValid()) {
            return CompareResult.SMALLER_THAN.value();
        }

        long otherValue = other.toUnit(mUnit).value();

        if (mValue > otherValue) {
            return CompareResult.GREATER_THAN.value();
        }
        if (mValue < otherValue) {
            return CompareResult.SMALLER_THAN.value();
        }

        return CompareResult.EQUAL_TO.value();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && equals((Time) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mValue, mUnit);
    }

    @Override
    public String toString() {
        return String.format("%d [%s]", mValue, mUnit.name());
    }

    private void checkValidForOperation(Time other) {
        if (!isValid()) {
            throw new IllegalStateException(getNotValidExceptionMessage(this));
        }

        if (!other.isValid()) {
            throw new IllegalArgumentException(getNotValidExceptionMessage(other));
        }
    }

    private String getNotValidExceptionMessage(Time time) {
        return String.format("time not valid: %s", time.toString());
    }
}
