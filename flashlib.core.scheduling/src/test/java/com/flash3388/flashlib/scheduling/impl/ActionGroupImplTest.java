package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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

        actionGroup.execute(mock(ActionControl.class));

        verify(mScheduler, times(1)).createExecutionContext(eq(actionGroup), eq(action));
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(1));
    }

    @Test
    public void execute_actionCurrentlyRunning_executesIt() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.execute(any())).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.execute(mock(ActionControl.class));

        verify(context, times(1)).execute(any());
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(1));
    }

    @Test
    public void execute_actionCurrentlyRunningIsFinished_removesIt() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.execute(any())).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.execute(mock(ActionControl.class));

        verify(context, times(1)).execute(any());
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(0));
    }

    @Test
    public void interrupted_actionIsRunning_cancelsIt() throws Exception {
        ExecutionContext context = mock(ExecutionContext.class);
        when(context.execute(any())).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.end(FinishReason.CANCELED);

        verify(context, times(1)).interrupt();
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(0));
    }

    @Test
    public void interrupted_hasWhenInterruptedCallback_callsIt() throws Exception {
        Runnable callback = mock(Runnable.class);

        ActionGroupImpl actionGroup = createSequential();
        actionGroup.whenInterrupted(callback);

        actionGroup.end(FinishReason.CANCELED);

        verify(callback, times(1)).run();
    }

    @Test
    public void initialize_forSequential_startsOneAction() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );
        mActions.addAll(actions);

        ActionGroupImpl actionGroup = createSequential();

        actionGroup.initialize();

        assertThat(mActionsToExecute, IsCollectionWithSize.hasSize(1));
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(1));
    }

    @Test
    public void initialize_forParallel_startsAllActions() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );
        mActions.addAll(actions);

        ActionGroupImpl actionGroup = createParallel();

        actionGroup.initialize();

        assertThat(mActionsToExecute, IsCollectionWithSize.hasSize(0));
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(2));
    }

    @Test
    public void initialize_forParallelRace_startsAllActions() throws Exception {
        List<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );
        mActions.addAll(actions);

        ActionGroupImpl actionGroup = createParallelRace();

        actionGroup.initialize();

        assertThat(mActionsToExecute, IsCollectionWithSize.hasSize(0));
        assertThat(mRunningActions, IsCollectionWithSize.hasSize(2));
    }

    @Test
    public void execute_forParallelActionsFinished_isFinishedTrue() throws Exception {
        ExecutionContext context1 = mock(ExecutionContext.class);
        when(context1.execute(any())).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context1);
        ExecutionContext context2 = mock(ExecutionContext.class);
        when(context2.execute(any())).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context2);

        ActionGroupImpl actionGroup = createParallelRace();

        ActionControl actionControl = mock(ActionControl.class);
        actionGroup.execute(actionControl);
        verify(actionControl, times(1)).finish();
    }

    @Test
    public void execute_forParallelRaceActionFinished_isFinishedTrue() throws Exception {
        ExecutionContext context1 = mock(ExecutionContext.class);
        when(context1.execute(any())).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context1);
        ExecutionContext context2 = mock(ExecutionContext.class);
        when(context2.execute(any())).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context2);

        ActionGroupImpl actionGroup = createParallelRace();

        ActionControl actionControl = mock(ActionControl.class);
        actionGroup.execute(actionControl);
        verify(actionControl, times(1)).finish();
    }

    @Test
    public void end_forParallelInterrupt_interruptsRemainingInEnd() throws Exception {
        ExecutionContext context1 = mock(ExecutionContext.class);
        when(context1.execute(any())).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context1);
        ExecutionContext context2 = mock(ExecutionContext.class);
        when(context2.execute(any())).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context2);

        ActionGroupImpl actionGroup = createParallel();

        actionGroup.execute(mock(ActionControl.class));
        actionGroup.end(FinishReason.CANCELED);

        verify(context1, times(1)).interrupt();
    }

    @Test
    public void end_forParallelRace_interruptsRemainingInEnd() throws Exception {
        ExecutionContext context1 = mock(ExecutionContext.class);
        when(context1.execute(any())).thenReturn(ExecutionContext.ExecutionResult.STILL_RUNNING);
        mRunningActions.add(context1);
        ExecutionContext context2 = mock(ExecutionContext.class);
        when(context2.execute(any())).thenReturn(ExecutionContext.ExecutionResult.FINISHED);
        mRunningActions.add(context2);

        ActionGroupImpl actionGroup = createParallelRace();

        actionGroup.execute(mock(ActionControl.class));
        actionGroup.end(FinishReason.CANCELED);

        verify(context1, times(1)).interrupt();
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