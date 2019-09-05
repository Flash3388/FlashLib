package com.flash3388.flashlib.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeTest {

    @RunWith(Parameterized.class)
    public static class ValidValueTest {

        @Parameterized.Parameter(0)
        public long mValue;
        @Parameterized.Parameter(1)
        public TimeUnit mTimeUnit;
        @Parameterized.Parameter(2)
        public boolean mIsValid;

        @Parameterized.Parameters(name = "time({0},{1}).isValid == {2}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {0, TimeUnit.MILLISECONDS, true},
                    {180, TimeUnit.MILLISECONDS, true},
                    {220, TimeUnit.SECONDS, true},
                    {-2, TimeUnit.MILLISECONDS, false},
                    {-164, TimeUnit.SECONDS, false}
            });
        }

        @Test
        public void isValid_forValue_indicatesExpectedResult() throws Exception {
            assertEquals(mIsValid, Time.of(mValue, mTimeUnit).isValid());
        }
    }

    @Test
    public void toUnits_forOtherUnits_convertsValueToOtherUnits() throws Exception {
        final long VALUE = 1;
        final Time TIME = Time.milliseconds(VALUE);
        final TimeUnit NEW_UNITS = TimeUnit.NANOSECONDS;
        final long NEW_VALUE = NEW_UNITS.convert(VALUE, TIME.unit());

        Time newTime = TIME.toUnit(NEW_UNITS);
        assertEquals(NEW_VALUE, newTime.value());
        assertEquals(NEW_UNITS, newTime.unit());
    }

    @Test
    public void add_withSameUnits_returnsAdditionOfValue() throws Exception {
        final TimeUnit UNIT = TimeUnit.MINUTES;
        final Time THIS = Time.of(1, UNIT);
        final Time OTHER = Time.of(1, UNIT);

        Time result = THIS.add(OTHER);
        assertEquals((THIS.value() + OTHER.value()), result.value());
    }

    @Test
    public void add_withDifferentUnits_returnsAdditionOfValueInThisUnit() throws Exception {
        final TimeUnit UNIT = TimeUnit.NANOSECONDS;
        final Time THIS = Time.of(1, UNIT);
        final Time OTHER = Time.milliseconds(1);

        Time result = THIS.add(OTHER);
        assertEquals((THIS.value() + UNIT.convert(OTHER.value(), OTHER.unit())), result.value());
    }

    @Test
    public void add_withSameUnits_returnsTimeWithThatUnit() throws Exception {
        final TimeUnit UNIT = TimeUnit.MINUTES;
        final Time THIS = Time.of(1, UNIT);
        final Time OTHER = Time.of(1, UNIT);

        Time result = THIS.add(OTHER);
        assertEquals(UNIT, result.unit());
    }

    @Test
    public void add_otherHasBiggerUnit_returnsTimeWithThisUnit() throws Exception {
        final TimeUnit THIS_UNIT = TimeUnit.NANOSECONDS;
        final TimeUnit OTHER_UNIT = TimeUnit.MILLISECONDS;
        final Time THIS = Time.of(1, THIS_UNIT);
        final Time OTHER = Time.of(1, OTHER_UNIT);

        Time result = THIS.add(OTHER);
        assertEquals(THIS_UNIT, result.unit());
    }

    @Test
    public void add_otherIsSmallerUnit_returnsTimeWithOtherUnit() throws Exception {
        final TimeUnit THIS_UNIT = TimeUnit.MINUTES;
        final TimeUnit OTHER_UNIT = TimeUnit.MILLISECONDS;
        final Time THIS = Time.of(1, THIS_UNIT);
        final Time OTHER = Time.of(1, OTHER_UNIT);

        Time result = THIS.add(OTHER);
        assertEquals(OTHER_UNIT, result.unit());
    }

    @Test(expected = IllegalStateException.class)
    public void add_thisNotValid_throwsIllegalStateException() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.milliseconds(1);

        THIS.add(OTHER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void add_otherNotValid_throwsIllegalArgumentException() throws Exception {
        final Time THIS = Time.milliseconds(1);
        final Time OTHER = Time.INVALID;

        THIS.add(OTHER);
    }

    @Test
    public void sub_withSameUnits_returnsSubtractionOfValue() throws Exception {
        final TimeUnit UNIT = TimeUnit.MINUTES;
        final Time THIS = Time.of(1, UNIT);
        final Time OTHER = Time.of(1, UNIT);

        Time result = THIS.sub(OTHER);
        assertEquals((THIS.value() - OTHER.value()), result.value());
    }

    @Test
    public void sub_withDifferentUnits_returnsSubtractionOfValueInThisUnit() throws Exception {
        final TimeUnit UNIT = TimeUnit.NANOSECONDS;
        final Time THIS = Time.of(1, UNIT);
        final Time OTHER = Time.milliseconds(1);

        Time result = THIS.sub(OTHER);
        assertEquals((THIS.value() - UNIT.convert(OTHER.value(), OTHER.unit())), result.value());
    }

    @Test
    public void sub_withSameUnits_returnsTimeWithThatUnit() throws Exception {
        final TimeUnit UNIT = TimeUnit.MINUTES;
        final Time THIS = Time.of(1, UNIT);
        final Time OTHER = Time.of(1, UNIT);

        Time result = THIS.sub(OTHER);
        assertEquals(UNIT, result.unit());
    }

    @Test
    public void sub_otherHasBiggerUnit_returnsTimeWithThisUnit() throws Exception {
        final TimeUnit THIS_UNIT = TimeUnit.NANOSECONDS;
        final TimeUnit OTHER_UNIT = TimeUnit.MILLISECONDS;
        final Time THIS = Time.of(1, THIS_UNIT);
        final Time OTHER = Time.of(1, OTHER_UNIT);

        Time result = THIS.sub(OTHER);
        assertEquals(THIS_UNIT, result.unit());
    }

    @Test
    public void sub_otherIsSmallerUnit_returnsTimeWithOtherUnit() throws Exception {
        final TimeUnit THIS_UNIT = TimeUnit.MINUTES;
        final TimeUnit OTHER_UNIT = TimeUnit.MILLISECONDS;
        final Time THIS = Time.of(1, THIS_UNIT);
        final Time OTHER = Time.of(1, OTHER_UNIT);

        Time result = THIS.sub(OTHER);
        assertEquals(OTHER_UNIT, result.unit());
    }

    @Test(expected = IllegalStateException.class)
    public void sub_thisNotValid_throwsIllegalStateException() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.milliseconds(1);

        THIS.sub(OTHER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sub_otherNotValid_throwsIllegalArgumentException() throws Exception {
        final Time THIS = Time.milliseconds(1);
        final Time OTHER = Time.INVALID;

        THIS.sub(OTHER);
    }

    @Test
    public void before_thisIsSmallerSameUnits_returnsTrue() throws Exception {
        final Time THIS = Time.milliseconds(1);
        final Time OTHER = Time.milliseconds(5);

        assertTrue(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsSmallerWithSmallerUnit_returnsTrue() throws Exception {
        final Time THIS = Time.milliseconds(1);
        final Time OTHER = Time.seconds(5);

        assertTrue(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsSmallerWithBiggerUnit_returnsTrue() throws Exception {
        final Time THIS = Time.seconds(1);
        final Time OTHER = Time.milliseconds(5000);

        assertTrue(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsBiggerSameUnits_returnsFalse() throws Exception {
        final Time THIS = Time.milliseconds(10);
        final Time OTHER = Time.milliseconds(5);

        assertFalse(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsBiggerWithSmallerUnit_returnsFalse() throws Exception {
        final Time THIS = Time.milliseconds(10000);
        final Time OTHER = Time.seconds(5);

        assertFalse(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsBiggerWithBiggerUnit_returnsFalse() throws Exception {
        final Time THIS = Time.seconds(1);
        final Time OTHER = Time.milliseconds(5);

        assertFalse(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsNotValidOtherIsValid_returnsTrue() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.milliseconds(5);

        assertTrue(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsNotValidOtherIsNotValid_returnsFalse() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.INVALID;

        assertFalse(THIS.before(OTHER));
    }

    @Test
    public void before_thisIsValidOtherIsNotValid_returnsFalse() throws Exception {
        final Time THIS = Time.milliseconds(2);
        final Time OTHER = Time.INVALID;

        assertFalse(THIS.before(OTHER));
    }

    @Test
    public void after_thisIsSmallerSameUnits_returnsFalse() throws Exception {
        final Time THIS = Time.milliseconds(1);
        final Time OTHER = Time.milliseconds(5);

        assertFalse(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsSmallerWithSmallerUnit_returnsFalse() throws Exception {
        final Time THIS = Time.milliseconds(1);
        final Time OTHER = Time.seconds(5);

        assertFalse(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsSmallerWithBiggerUnit_returnsFalse() throws Exception {
        final Time THIS = Time.seconds(1);
        final Time OTHER = Time.milliseconds(5000);

        assertFalse(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsBiggerSameUnits_returnsTrue() throws Exception {
        final Time THIS = Time.milliseconds(10);
        final Time OTHER = Time.milliseconds(5);

        assertTrue(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsBiggerWithSmallerUnit_returnsTrue() throws Exception {
        final Time THIS = Time.milliseconds(10000);
        final Time OTHER = Time.seconds(5);

        assertTrue(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsBiggerWithBiggerUnit_returnsTrue() throws Exception {
        final Time THIS = Time.seconds(1);
        final Time OTHER = Time.milliseconds(5);

        assertTrue(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsNotValidOtherIsValid_returnsFalse() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.milliseconds(5);

        assertFalse(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsNotValidOtherIsNotValid_returnsFalse() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.INVALID;

        assertFalse(THIS.after(OTHER));
    }

    @Test
    public void after_thisIsValidOtherIsNotValid_returnsTrue() throws Exception {
        final Time THIS = Time.milliseconds(2);
        final Time OTHER = Time.INVALID;

        assertTrue(THIS.after(OTHER));
    }

    @Test
    public void equals_thisIsNotValidOtherIsValid_returnsFalse() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.milliseconds(5);

        assertFalse(THIS.equals(OTHER));
    }

    @Test
    public void equals_thisIsNotValidOtherIsNotValid_returnsTrue() throws Exception {
        final Time THIS = Time.INVALID;
        final Time OTHER = Time.INVALID;

        assertTrue(THIS.equals(OTHER));
    }

    @Test
    public void equals_thisIsValidOtherIsNotValid_returnsFalse() throws Exception {
        final Time THIS = Time.milliseconds(2);
        final Time OTHER = Time.INVALID;

        assertFalse(THIS.equals(OTHER));
    }
}