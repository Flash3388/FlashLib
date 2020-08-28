package com.flash3388.flashlib.util.concurrent;

import com.beans.BooleanProperty;
import com.beans.Properties;
import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.time.Time;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SleeperTest {

    @Test
    public void sleepWhileConditionMet_sleepTimeCloseToMin_periodSleepNotBelowMin() throws Exception {
        Sleeper sleeper = spy(new Sleeper());
        doNothing().when(sleeper).sleepMs(anyLong());

        sleeper.sleepWhileConditionMet(()->true, Time.milliseconds(6));

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(sleeper, atLeastOnce()).sleepMs(argumentCaptor.capture());

        long periodSleepTime = argumentCaptor.getValue();
        assertThat(periodSleepTime, greaterThanOrEqualTo(Sleeper.MIN_SLEEP_PERIOD_MS));
    }

    @Test
    public void sleepWhileConditionMet_conditionPassedBeforeSleepTime_stopsCallingSleep() throws Exception {
        Sleeper sleeper = spy(new Sleeper());
        BooleanProperty condition = Properties.of(true);

        doAnswer((invocation)-> {
            condition.setAsBoolean(false);
            return null;
        }).when(sleeper).sleepMs(anyLong());

        sleeper.sleepWhileConditionMet(condition, Time.milliseconds(2000));

        verify(sleeper, times(1)).sleepMs(anyLong());
    }

    @Test
    public void sleepWhileConditionMet_conditionDoesNotChangeBeforeTime_sleepsAllottedTime() throws Exception {
        final long SLEEP_TIME_MS = 100;
        Sleeper sleeper = new Sleeper();

        long start = System.nanoTime();
        sleeper.sleepWhileConditionMet(Suppliers.of(true), Time.milliseconds(SLEEP_TIME_MS));
        long passed = (long) ((System.nanoTime() - start) * 1e-6);

        long difference = Math.abs(passed - SLEEP_TIME_MS);
        assertThat(difference, lessThanOrEqualTo(SLEEP_TIME_MS / 15));
    }

}