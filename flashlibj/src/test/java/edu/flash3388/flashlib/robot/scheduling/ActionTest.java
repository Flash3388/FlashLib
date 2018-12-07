package edu.flash3388.flashlib.robot.scheduling;

import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;
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

        mAction = new FakeAction(mScheduler, mClock);
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
    public void hasTimeoutReached_timeoutWasReached_returnsTrue() throws Exception {
        final Time TIMEOUT = Time.millis(1000);
        final Time START_TIME = Time.millis(500);
        final Time CLOCK_TIME = Time.millis(2000);

        mAction.setTimeout(TIMEOUT);
        when(mClock.currentTime()).thenReturn(START_TIME);

        mAction.start();
        mAction.run();

        when(mClock.currentTime()).thenReturn(CLOCK_TIME);

        assertTrue(mAction.wasTimeoutReached());
    }

    @Test
    public void hasTimeoutReached_timeoutWasNotReached_returnsFalse() throws Exception {
        final Time TIMEOUT = Time.millis(1000);
        final Time START_TIME = Time.millis(500);
        final Time CLOCK_TIME = Time.millis(500);

        when(mClock.currentTime()).thenReturn(START_TIME);
        mAction.setTimeout(TIMEOUT);

        mAction.start();
        mAction.run();

        when(mClock.currentTime()).thenReturn(CLOCK_TIME);

        assertFalse(mAction.wasTimeoutReached());
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