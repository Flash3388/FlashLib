package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.robot.ClockMock;
import com.flash3388.flashlib.robot.RunningRobotMock;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PeriodicActionTest {

    private Clock mClock;

    @BeforeEach
    public void setup() throws Exception {
        mClock = ClockMock.mockInvalidTimeClock();

        RunningRobotMock.mockRobotWithDependencies();
        RunningRobotMock.mockRobotWithClock(mClock);
    }

    @Test
    public void execute_firstRun_runsRunnable() throws Exception {
        Runnable runnable = mock(Runnable.class);
        Time period = Time.seconds(1);
        when(mClock.currentTime()).thenReturn(Time.seconds(1));

        PeriodicAction action = new PeriodicAction(mClock, runnable, period);
        action.initialize();
        action.execute();

        verify(runnable, times(1)).run();
    }

    @Test
    public void execute_notFirstRunTimeHasElapsed_runsRunnable() throws Exception {
        Runnable runnable = mock(Runnable.class);
        Time nextRun = Time.seconds(1);
        when(mClock.currentTime()).thenReturn(nextRun);

        PeriodicAction action = new PeriodicAction(mClock, runnable, Time.seconds(1), nextRun);
        action.execute();

        verify(runnable, times(1)).run();
    }

    @Test
    public void execute_notFirstRunTimeNotElapsed_notRunsRunnable() throws Exception {
        Runnable runnable = mock(Runnable.class);
        Time nextRun = Time.seconds(2);
        when(mClock.currentTime()).thenReturn(Time.seconds(1));

        PeriodicAction action = new PeriodicAction(mClock, runnable, Time.seconds(1), nextRun);
        action.execute();

        verify(runnable, never()).run();
    }
}