package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class ActionGroupImplTest {

    private Scheduler mScheduler;

    private Collection<Action> mActions;
    private Queue<Action> mActionsToExecute;
    private Collection<ExecutionContext> mRunningActions;

    @BeforeEach
    public void setup() {
        mScheduler = mock(Scheduler.class);

        mActions = new ArrayList<>();
        mActionsToExecute = new ArrayDeque<>();
        mRunningActions = new ArrayList<>();
    }

    @Test
    public void execute_noActionCurrentlyRunning_startsNextAction() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        mActionsToExecute.add(action);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.execute();

        verify(mScheduler, times(1)).createExecutionContext(eq(actionGroup), eq(action));
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(1));
    }

    @Test
    public void execute_actionCurrentlyRunning_executesIt() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.execute()).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.execute();

        verify(context, times(1)).execute();
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(1));
    }

    @Test
    public void execute_actionCurrentlyRunningIsFinished_removesIt() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.execute()).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.execute();

        verify(context, times(1)).execute();
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(0));
    }

    @Test
    public void interrupted_actionIsRunning_cancelsIt() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.execute()).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.end(true);

        verify(context, times(1)).interrupt();
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(0));
    }

    @Test
    public void interrupted_hasWhenInterruptedCallback_callsIt() throws Exception {
        Runnable callback = mock(Runnable.class);

        ActionGroupImpl actionGroup = createSequential();
        actionGroup.whenInterrupted(callback);

        actionGroup.end(true);

        verify(callback, times(1)).run();
    }

    private ActionGroupImpl createSequential() {
        return new ActionGroupImpl(mScheduler, mock(Logger.class),
                GroupPolicy.sequential(),
                mActions, mActionsToExecute, mRunningActions);
    }

    private ActionGroupImpl createParallel() {
        return new ActionGroupImpl(mScheduler, mock(Logger.class),
                GroupPolicy.parallel(),
                mActions, mActionsToExecute, mRunningActions);
    }

    private ActionGroupImpl createParallelRace() {
        return new ActionGroupImpl(mScheduler, mock(Logger.class),
                GroupPolicy.parallelRace(),
                mActions, mActionsToExecute, mRunningActions);
    }
}