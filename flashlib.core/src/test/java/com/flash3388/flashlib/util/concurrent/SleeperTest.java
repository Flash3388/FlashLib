package com.flash3388.flashlib.util.concurrent;

import com.beans.BooleanProperty;
import com.beans.Properties;
import com.flash3388.flashlib.time.Time;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
}