package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.junit.Before;
import org.junit.Test;

import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.makeActionCancelable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActionContextTest {

    private Action mAction;
    private Clock mClock;

    private ActionContext mActionContext;

    @Before
    public void setUp() throws Exception {
        mAction = mock(Action.class);
        mockActionFinished(false);
        mockActionTimeout(Time.INVALID);
        makeActionCancelable(mAction);

        mClock = mock(Clock.class);
        when(mClock.currentTime()).thenReturn(Time.INVALID);

        mActionContext = new ActionContext(mAction, mClock);
    }

    @Test
    public void run_actionWasNotInitialized_initializeIsCalled() throws Exception {
        mActionContext.prepareForRun();
        mActionContext.run();

        verify(mAction, times(1)).initialize();
    }

    @Test
    public void run_actionWasAlreadyInitialized_initializeIsNotCalledAgain() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mActionContext.prepareForRun();
        mActionContext.run();
        mActionContext.run();

        verify(mAction, times(1)).initialize();
    }

    @Test
    public void run_actionIsRunning_executeCalledEachTime() throws Exception {
        final int RUN_TIMES = 10;
        mockClockTime(Time.milliseconds(1));

        mActionContext.prepareForRun();
        for (int i = 0; i < RUN_TIMES; i++) {
            mActionContext.run();
        }

        verify(mAction, times(RUN_TIMES)).execute();
    }

    @Test
    public void run_actionInitializedAndWasCanceled_interruptedCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mActionContext.prepareForRun();
        mActionContext.run();
        mActionContext.runCanceled();

        verify(mAction, times(1)).interrupted();
    }

    @Test
    public void run_actionNotInitializedAndWasCanceled_interruptedNotCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mActionContext.prepareForRun();
        mActionContext.runCanceled();

        verify(mAction, never()).interrupted();
    }

    @Test
    public void run_actionInitializedAndHasEnded_endCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mActionContext.prepareForRun();
        mActionContext.run();
        mActionContext.runFinished();

        verify(mAction, times(1)).end();
    }

    @Test
    public void run_actionNotInitializedAndHasEnded_endNotCalled() throws Exception {
        mockClockTime(Time.milliseconds(1));

        mActionContext.prepareForRun();
        mActionContext.runFinished();

        verify(mAction, never()).end();
    }

    @Test
    public void run_isFinishedReturnsTrue_returnsFalse() throws Exception {
        mockClockTime(Time.milliseconds(1));
        mockActionFinished(true);

        mActionContext.prepareForRun();
        assertFalse(mActionContext.run());
    }

    @Test
    public void run_isFinishedReturnsFalse_returnsTrue() throws Exception {
        mockClockTime(Time.milliseconds(1));
        mockActionFinished(false);

        mActionContext.prepareForRun();
        assertTrue(mActionContext.run());
    }

    @Test
    public void wasTimeoutReached_timeoutWasReached_returnsTrue() throws Exception {
        final Time TIMEOUT = Time.milliseconds(1000);
        final Time START_TIME = Time.milliseconds(500);
        final Time CLOCK_TIME = Time.milliseconds(2000);

        mockActionTimeout(TIMEOUT);
        mockClockTime(START_TIME);

        mActionContext.prepareForRun();
        mActionContext.run();

        mockClockTime(CLOCK_TIME);

        assertTrue(mActionContext.wasTimeoutReached());
    }

    @Test
    public void wasTimeoutReached_timeoutWasNotReached_returnsFalse() throws Exception {
        final Time TIMEOUT = Time.milliseconds(1000);
        final Time START_TIME = Time.milliseconds(500);
        final Time CLOCK_TIME = Time.milliseconds(500);

        mockClockTime(START_TIME);
        mockActionTimeout(TIMEOUT);

        mActionContext.prepareForRun();
        mActionContext.run();

        mockClockTime(CLOCK_TIME);

        assertFalse(mActionContext.wasTimeoutReached());
    }

    private void mockClockTime(Time time) {
        when(mClock.currentTime()).thenReturn(time);
    }

    private void mockActionFinished(boolean isFinished) {
        when(mAction.isFinished()).thenReturn(isFinished);
    }

    private void mockActionTimeout(Time timeout) {
        when(mAction.getTimeout()).thenReturn(timeout);
    }
}