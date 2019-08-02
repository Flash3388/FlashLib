package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.JavaMillisClock;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SequentialActionGroupTest {

    @Test
    public void start_withRunningFirstAction_runsThatAction() throws Exception {
        SequentialAction firstAction = mock(SequentialAction.class);
        when(firstAction.isRunning()).thenReturn(true);
        when(firstAction.run()).thenReturn(true);

        SequentialActionGroup sequentialActionGroup = new SequentialActionGroup(new Scheduler(), new JavaMillisClock(), firstAction);

        sequentialActionGroup.start();
        sequentialActionGroup.run();

        verify(firstAction, times(1)).run();
    }

    @Test
    public void start_withNotRunningFirstAction_removesThatAction() throws Exception {
        SequentialAction firstAction = mock(SequentialAction.class);
        when(firstAction.isRunning()).thenReturn(true);
        when(firstAction.run()).thenReturn(false);

        SequentialActionGroup sequentialActionGroup = new SequentialActionGroup(new Scheduler(), new JavaMillisClock(), firstAction);

        sequentialActionGroup.start();
        sequentialActionGroup.run();

        verify(firstAction, times(1)).removed();
    }

    @Test
    public void start_currentActionHasNextAction_startsTheNewAction() throws Exception {
        Action secondAction = mock(Action.class);
        SequentialAction firstAction = spy(new ActionWithNext(new Scheduler(), new JavaMillisClock(), secondAction));

        doNothing().when(firstAction).setParent(any(Action.class));
        when(firstAction.isRunning()).thenReturn(true);
        when(firstAction.run()).thenReturn(false);

        when(secondAction.isRunning()).thenReturn(false);

        SequentialActionGroup sequentialActionGroup = new SequentialActionGroup(new Scheduler(), new JavaMillisClock(), firstAction);

        sequentialActionGroup.start();
        sequentialActionGroup.run();
        sequentialActionGroup.run();

        verify(secondAction, times(1)).markStarted();
    }

    @Test
    public void start_nextActionIsSequentialAction_checksForItsNext() throws Exception {
        Action thirdAction = mock(Action.class);
        SequentialAction secondAction = spy(new ActionWithNext(new Scheduler(), new JavaMillisClock(), thirdAction));
        SequentialAction firstAction = spy(new ActionWithNext(new Scheduler(), new JavaMillisClock(), secondAction));

        doNothing().when(firstAction).setParent(any(Action.class));
        when(firstAction.isRunning()).thenReturn(true);
        when(firstAction.run()).thenReturn(false);

        when(secondAction.isRunning()).thenReturn(true);
        when(secondAction.run()).thenReturn(false);

        SequentialActionGroup sequentialActionGroup = new SequentialActionGroup(new Scheduler(), new JavaMillisClock(), firstAction);

        sequentialActionGroup.start();
        sequentialActionGroup.run();
        sequentialActionGroup.run();
        sequentialActionGroup.run();

        verify(thirdAction, times(1)).markStarted();
    }

    @Test
    public void cancel_actionIsInterrupted_currentActionIsCanceled() throws Exception {
        SequentialAction action = mock(SequentialAction.class);
        when(action.isRunning()).thenReturn(true);
        when(action.run()).thenReturn(true);

        SequentialActionGroup sequentialActionGroup = new SequentialActionGroup(new Scheduler(), new JavaMillisClock(), action);

        sequentialActionGroup.start();
        sequentialActionGroup.run();
        sequentialActionGroup.cancel();
        sequentialActionGroup.removed();

        verify(action, times(1)).markCanceled();
    }

    @Test(expected = IllegalStateException.class)
    public void start_doesNotHaveRequirementsForNextActon_throwsIllegalStateException() throws Exception {
        Action secondAction = mock(Action.class);
        SequentialAction firstAction = spy(new ActionWithNext(new Scheduler(), new JavaMillisClock(), secondAction));

        doNothing().when(firstAction).setParent(any(Action.class));
        when(firstAction.isRunning()).thenReturn(true);
        when(firstAction.run()).thenReturn(false);

        when(secondAction.isRunning()).thenReturn(false);
        when(secondAction.getRequirements()).thenReturn(Collections.singleton(mock(Subsystem.class)));

        SequentialActionGroup sequentialActionGroup = new SequentialActionGroup(new Scheduler(), new JavaMillisClock(), firstAction);

        sequentialActionGroup.start();
        sequentialActionGroup.run();
        sequentialActionGroup.run();
    }

    private static class ActionWithNext extends SequentialAction {

        private final Action mNext;

        private ActionWithNext(Scheduler scheduler, Clock clock, Action next) {
            super(scheduler, clock);
            mNext = next;
        }

        @Override
        protected void execute() {
        }

        @Override
        protected void end() {
            runNext(mNext);
        }
    }
}