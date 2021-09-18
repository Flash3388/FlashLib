package com.flash3388.flashlib.time;

import com.flash3388.flashlib.util.CompareResult;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Represents time data, including both the actual time value and the time unit.
 *
 * @since FlashLib 2.0.0
 */
public class Time implements Comparable<Time> {

    public static final long INVALID_VALUE = -1L;
    public static final Time INVALID = new Time(INVALID_VALUE, TimeUnit.MILLISECONDS);

    private final long mValue;
    private final TimeUnit mUnit;

    Time(long value, TimeUnit unit) {
        mValue = value;
        mUnit = Objects.requireNonNull(unit, "time unit");
    }

    /**
     * Creates a new time value.
     *
     * @param value a non-negative time value
     * @param unit the unit
     *
     * @return a new {@link Time} representing the given value.
     */
    public static Time of(long value, TimeUnit unit) {
        if (value < 0) {
            throw new IllegalArgumentException("time value must be non-negative");
        }

        return new Time(value, unit);
    }

    /**
     * Creates a new time value with milliseconds unit.
     *
     * @param valueMs a non-negative time value in milliseconds.
     *
     * @return a new {@link Time} representing the given value.
     */
    public static Time milliseconds(long valueMs) {
        return of(valueMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a new time value with seconds unit.
     *
     * @param valueSeconds a non-negative time value in seconds.
     *
     * @return a new {@link Time} representing the given value.
     */
    public static Time seconds(long valueSeconds) {
        return of(valueSeconds, TimeUnit.SECONDS);
    }

    /**
     * Creates a new time value with seconds unit.
     * <p>
     *     Unlike with {@link #seconds(long)}, the created time object is with {@link TimeUnit#MILLISECONDS},
     *     based on the given seconds value. This allows precision when defining time with decimal numbers for seconds.
     * </p>
     *
     * @param valueSeconds a non-negative time value in seconds.
     *
     * @return a new {@link Time} representing the given value.
     */
    public static Time seconds(double valueSeconds) {
        return milliseconds((long) (valueSeconds * 1000));
    }

    /**
     * Creates a new time value with minutes unit.
     *
     * @param valueMinutes a non-negative time value in minutes.
     *
     * @return a new {@link Time} representing the given value.
     */
    public static Time minutes(long valueMinutes) {
        return of(valueMinutes, TimeUnit.MINUTES);
    }

    /**
     * Creates a new time value with minutes unit.
     * <p>
     *     Unlike with {@link #seconds(long)}, the created time object is with {@link TimeUnit#MILLISECONDS},
     *     based on the given minutes value. This allows precision when defining time with decimal numbers for minutes.
     * </p>
     *
     * @param valueMinutes a non-negative time value in minutes.
     *
     * @return a new {@link Time} representing the given value.
     */

    public static Time minutes(double valueMinutes) {
        return milliseconds((long) (valueMinutes * 3600));
    }

    /**
     * Gets the time value.
     *
     * @return time value.
     */
    public long value() {
        return mValue;
    }

    /**
     * Gets the time unit.
     *
     * @return time unit.
     */
    public TimeUnit unit() {
        return mUnit;
    }

    /**
     * Converts the current time value to another unit.
     *
     * @param newTimeUnit unit to convert value to.
     * @return
     * <ul>
     *     <li>if not {@link #isValid()}, returns an <code>invalid</code> time object with the given units</li>
     *     <li>if <code>newTimeUnit</code> is the same as {@link #unit()}: returns <b>this</b></li>
     *     <li>otherwise, returns a new {@link Time} object with the value converted to the given unit</li>
     * </ul>
     */
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

    /**
     * Returns {@link #value()} in {@link TimeUnit#MILLISECONDS}.
     * <p>Equivalent to using:</p>
     * <pre>
     *     time.toUnit(TimeUnit.MILLISECONDS).value()
     * </pre>
     *
     *
     * @return value in milliseconds.
     */
    public long valueAsMillis() {
        return toUnit(TimeUnit.MILLISECONDS).value();
    }

    /**
     * Returns {@link #value()} in {@link TimeUnit#SECONDS}, as a floating point value,
     * maintaining the data if the value is lower than <b>1</b> seconds.
     * <p>Equivalent to using:</p>
     * <pre>
     *     time.valueAsMillis() * 1e-3
     * </pre>
     *
     *
     * @return value in seconds.
     */
    public double valueAsSeconds() {
        long millis = valueAsMillis();
        return millis * 1e-3;
    }

    /**
     * Gets whether or not the time value is valid.
     * <p>
     *      An <em>invalid</em> time
     *      is replacement for using <b>null</b> values to indicate a lack of value, and
     *      can be used when initializing or setting {@link Time} objects to <em>empty</em>.
     *      So instead of:
     * </p>
     * <pre>
     *     private Time timeout;
     *
     *     public void clearTimeoutValue() {
     *         timeout = null;
     *     }
     * </pre>
     * <p>This can be done:</p>
     * <pre>
     *     private Time timeout;
     *
     *     public void clearTimeoutValue() {
     *         timeout = Time.INVALID;
     *     }
     * </pre>
     * <p>
     *      The advantage of {@link Time#INVALID} is that it won't cause a {@link NullPointerException}. However
     *      some operations may fail when using it, so it is recommended to check this method before.
     * </p>
     *
     * @return <b>true</b> if valid, <b>false</b> otherwise.
     */
    public boolean isValid() {
        return mValue >= 0;
    }

    /**
     * Performs addition between this and the given time, producing a time value, which
     * is the sum of both.
     * <p>
     *     The resulting time might have a different unit. The select unit for the result
     *     is the smallest unit out of both times, allowing to keep the best precision.
     * </p>
     *
     * @param other time to add.
     *
     * @return sum of this and the given time.
     */
    public Time add(Time other) {
        checkValidForOperation(other);

        TimeUnit smallerUnit = UnitComparing.smallerUnit(mUnit, other.unit());
        long newValue = smallerUnit.convert(mValue, mUnit) + other.toUnit(smallerUnit).value();
        return new Time(newValue, smallerUnit);
    }

    /**
     * Performs subtraction between this and the given time, producing a time value, which
     * is the subtraction result <code>this - other</code>
     * <p>
     *     The resulting time might have a different unit. The select unit for the result
     *     is the smallest unit out of both times, allowing to keep the best precision.
     * </p>
     * <p>
     *     If <code>other.after(this)</code>, the resulting time would be invalid.
     * </p>
     *
     * @param other time to subtract.
     *
     * @return subtraction of this and the given time.
     * @see #isValid()
     */
    public Time sub(Time other) {
        checkValidForOperation(other);

        TimeUnit smallerUnit = UnitComparing.smallerUnit(mUnit, other.unit());
        long newValue = smallerUnit.convert(mValue, mUnit) - other.toUnit(smallerUnit).value();
        return new Time(newValue, smallerUnit);
    }

    /**
     * Gets whether or not this time is before the given time.
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this &lt; other</code>, <b>false</b> otherwise.
     *
     * @see #lessThan(Time)
     */
    public boolean before(Time other) {
        return lessThan(other);
    }

    /**
     * Gets whether or not this time value is less than the given time if they were the same unit.
     * <p>
     *     The resulting time might have a different unit. The select unit for the result
     *     is the smallest unit out of both times, allowing to keep the best precision.
     * </p>
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this &lt; other</code>, <b>false</b> otherwise.
     *
     * @see #lessThanOrEquals(Time)
     * @see #largerThan(Time)
     */
    public boolean lessThan(Time other) {
        return CompareResult.SMALLER_THAN.is(compareTo(other));
    }

    /**
     * Gets whether or not this time value is less than or equal to the given time if they were the same unit.
     * <p>
     *     The resulting time might have a different unit. The select unit for the result
     *     is the smallest unit out of both times, allowing to keep the best precision.
     * </p>
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this &lt;= other</code>, <b>false</b> otherwise.
     *
     * @see #lessThan(Time)
     * @see #largerThan(Time)
     */
    public boolean lessThanOrEquals(Time other) {
        int compareResult = compareTo(other);
        return CompareResult.in(compareResult, CompareResult.SMALLER_THAN, CompareResult.EQUAL_TO);
    }

    /**
     * Gets whether or not this time is after the given time.
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this &gt; other</code>, <b>false</b> otherwise.
     *
     * @see #largerThan(Time)
     */
    public boolean after(Time other) {
        return largerThan(other);
    }

    /**
     * Gets whether or not this time value is larger than the given time if they were the same unit.
     * <p>
     *     The resulting time might have a different unit. The select unit for the result
     *     is the smallest unit out of both times, allowing to keep the best precision.
     * </p>
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this &gt; other</code>, <b>false</b> otherwise.
     *
     * @see #lessThan(Time)
     * @see #largerThanOrEquals(Time)
     */
    public boolean largerThan(Time other) {
        return CompareResult.GREATER_THAN.is(compareTo(other));
    }

    /**
     * Gets whether or not this time value is larger than or equal to the given time if they were the same unit.
     * <p>
     *     The resulting time might have a different unit. The select unit for the result
     *     is the smallest unit out of both times, allowing to keep the best precision.
     * </p>
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this &gt;= other</code>, <b>false</b> otherwise.
     *
     * @see #lessThan(Time)
     * @see #largerThan(Time)
     */
    public boolean largerThanOrEquals(Time other) {
        int compareResult = compareTo(other);
        return CompareResult.in(compareResult, CompareResult.GREATER_THAN, CompareResult.EQUAL_TO);
    }

    /**
     * Gets whether or not this time equals the given time, if they were the same unit.
     *
     * @param other time to compare against.
     *
     * @return <b>true</b> if <code>this == other</code>, <b>false</b> otherwise.
     */
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
        int hash = Long.hashCode(mValue);
        hash = hash * 31 + mUnit.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid Time";
        }

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
        return String.format("time not valid: %d [%s]", time.mValue, time.mUnit);
    }

    /**
     * Gets the smallest time of the given times. Can be thought of as like {@link Math#min(int, int)}
     * for {@link Time}.
     *
     * @param times times to find the earliest out of.
     *
     * @return the minimum time
     */
    public static Time earliest(Time... times) {
        if (times.length < 1) {
            throw new IllegalArgumentException("expected times");
        }

        Time min = null;
        for (Time time : times) {
            if (min == null || time.before(min)) {
                min = time;
            }
        }

        return min;
    }

    /**
     * Gets the largest time of the given times. Can be thought of as like {@link Math#max(int, int)}
     * for {@link Time}.
     *
     * @param times times to find the latest out of.
     *
     * @return the maximum time
     */
    public static Time latest(Time... times) {
        if (times.length < 1) {
            throw new IllegalArgumentException("expected times");
        }

        Time max = null;
        for (Time time : times) {
            if (max == null || time.after(max)) {
                max = time;
            }
        }

        return max;
    }
}
