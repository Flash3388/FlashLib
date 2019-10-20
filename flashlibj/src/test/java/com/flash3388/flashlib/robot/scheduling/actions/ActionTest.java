package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ActionTest {

    private Scheduler mScheduler;

    private Action mAction;

    @Before
    public void setUp() throws Exception {
        mScheduler = mock(Scheduler.class);

        mAction = spy(new FakeAction(mScheduler));
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

    public class FakeAction extends Action {

        FakeAction(Scheduler scheduler) {
            super(scheduler, Time.INVALID);
        }

        @Override
        protected void execute() {
        }

        @Override
        protected void end() {
        }
    }
}