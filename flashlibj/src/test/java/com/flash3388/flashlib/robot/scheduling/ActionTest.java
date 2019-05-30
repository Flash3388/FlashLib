package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ActionTest {

    private Scheduler mScheduler;
    private Clock mClock;

    private Action mAction;

    @Before
    public void setUp() throws Exception {
        mScheduler = mock(Scheduler.class);
        mClock = mock(Clock.class);

        mAction = spy(new FakeAction(mScheduler, mClock));
    }

    @Test
    public void start_actionNotRunning_actionAddedToScheduler() throws Exception {
        mAction.start();

        verify(mScheduler, times(1)).add(eq(mAction));
    }

    @Test
    public void start_actionAlreadyStarted_actionNotAddedToScheduler() throws Exception {
        mAction.start();
        mAction.start();

        verify(mScheduler, times(1)).add(any(Action.class));
    }

    @Test
    public void start_actionWasStartedAndStopped_actionAddedToScheduler() throws Exception {
        mAction.start();
        mAction.removed();
        mAction.start();

        verify(mScheduler, times(2)).add(eq(mAction));
    }

    @Test
    public void start_actionWasNotRunning_actionIsMarkAsRunning() throws Exception {
        mAction.start();

        assertTrue(mAction.isRunning());
    }

    @Test
    public void cancel_actionWasRunning_actionIsMarkAsCanceled() throws Exception {
        mAction.start();
        mAction.cancel();

        assertTrue(mAction.isCanceled());
    }

    @Test
    public void run_actionWasNotInitialized_initializeIsCalled() throws Exception {
        mAction.start();
        mAction.run();

        verify(mAction, times(1)).initialize();
    }

    @Test
    public void run_actionWasAlreadyInitialized_initializeIsNotCalledAgain() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mAction.start();
        mAction.run();
        mAction.run();

        verify(mAction, times(1)).initialize();
    }

    @Test
    public void run_actionIsRunning_executeCalledEachTime() throws Exception {
        final int RUN_TIMES = 10;
        mockClockTime(Time.milliseconds(1));

        mAction.start();
        for (int i = 0; i < RUN_TIMES; i++) {
            mAction.run();
        }

        verify(mAction, times(RUN_TIMES)).execute();
    }

    @Test
    public void run_actionInitializedAndWasCanceled_interruptedCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mAction.start();
        mAction.run();
        mAction.cancel();
        mAction.removed();

        verify(mAction, times(1)).interrupted();
    }

    @Test
    public void run_actionNotInitializedAndWasCanceled_interruptedNotCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mAction.start();
        mAction.cancel();
        mAction.removed();

        verify(mAction, never()).interrupted();
    }

    @Test
    public void run_actionInitializedAndHasEnded_endCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mAction.start();
        mAction.run();
        mAction.removed();

        verify(mAction, times(1)).end();
    }

    @Test
    public void run_actionNotInitializedAndHasEnded_endNotCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mAction.start();
        mAction.removed();

        verify(mAction, never()).end();
    }

    @Test
    public void run_isFinishedReturnsTrue_returnsFalse() throws Exception {
        mockClockTime(Time.milliseconds(1));
        mockActionFinished(true);

        mAction.start();
        assertFalse(mAction.run());
    }

    @Test
    public void run_isFinishedReturnsFalse_returnsTrue() throws Exception {
        mockClockTime(Time.milliseconds(1));
        mockActionFinished(false);

        mAction.start();
        assertTrue(mAction.run());
    }

    @Test
    public void wasTimeoutReached_timeoutWasReached_returnsTrue() throws Exception {
        final Time TIMEOUT = Time.milliseconds(1000);
        final Time START_TIME = Time.milliseconds(500);
        final Time CLOCK_TIME = Time.milliseconds(2000);

        mAction.setTimeout(TIMEOUT);
        mockClockTime(START_TIME);

        mAction.start();
        mAction.run();

        mockClockTime(CLOCK_TIME);

        assertTrue(mAction.wasTimeoutReached());
    }

    @Test
    public void wasTimeoutReached_timeoutWasNotReached_returnsFalse() throws Exception {
        final Time TIMEOUT = Time.milliseconds(1000);
        final Time START_TIME = Time.milliseconds(500);
        final Time CLOCK_TIME = Time.milliseconds(500);

        mockClockTime(START_TIME);
        mAction.setTimeout(TIMEOUT);

        mAction.start();
        mAction.run();

        mockClockTime(CLOCK_TIME);

        assertFalse(mAction.wasTimeoutReached());
    }

    private void mockClockTime(Time time) {
        when(mClock.currentTime()).thenReturn(time);
    }

    private void mockActionFinished(boolean isFinished) {
        when(mAction.isFinished()).thenReturn(isFinished);
    }

    public class FakeAction extends Action {

        FakeAction(Scheduler scheduler, Clock clock) {
            super(scheduler, clock, Time.INVALID);
        }

        @Override
        protected void execute() {
        }

        @Override
        protected void end() {
        }
    }
}