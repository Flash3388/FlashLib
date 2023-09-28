package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.SchedulerModeMock;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.time.ClockMock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SingleThreadedSchedulerTest {

    private Map<ActionInterface, RunningActionContext> mPendingActions;
    private Map<ActionInterface, RunningActionContext> mRunningActions;
    private Map<Requirement, ActionInterface> mRequirementsUsage;
    private Map<Subsystem, ActionInterface> mDefaultActions;

    private SingleThreadedScheduler mScheduler;

    @BeforeEach
    public void setUp() throws Exception {
        mPendingActions = new HashMap<>();
        mRunningActions = new HashMap<>();
        mRequirementsUsage = new HashMap<>();
        mDefaultActions = new HashMap<>();

        mScheduler = new SingleThreadedScheduler(
                ClockMock.mockInvalidTimeClock(),
                new FlashLibMainThread.Stub(),
                new StoredObject.Stub(),
                mPendingActions,
                mRunningActions,
                new ArrayList<>(),
                mRequirementsUsage,
                mDefaultActions);
    }

    @Test
    public void start_actionNotRunningNoConflicts_putsActionInRunningAndUpdatesRequirements() throws Exception {
        Requirement requirement = mock(Requirement.class);

        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(action);

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(requirement, action));
    }

    @Test
    public void start_actionNotRunningHasNonPreferredConflicts_cancelsAndEndsConflictingStartsNew() throws Exception {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mRequirementsUsage.put(requirement, action);
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.getAction()).thenReturn(action);
        mRunningActions.put(action, context);

        ActionInterface newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(newAction);

        verify(context, times(1)).markForCancellation();
        verify(context, times(1)).iterate();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRunningActions, IsMapContaining.hasKey(newAction));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(requirement, newAction));
    }

    @Test
    public void start_actionNotRunningHasConflicts_marksConflictingForCancellation() throws Exception {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        mRequirementsUsage.put(requirement, action);
        mRunningActions.put(action, context);

        ActionInterface newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(newAction);

        verify(context, times(1)).markForCancellation();
    }

    @Test
    public void start_actionIsRunning_throwsIllegalArgumentException() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        mRunningActions.put(action, mock(RunningActionContext.class));

        assertThrows(IllegalArgumentException.class, ()-> {
            mScheduler.start(action);
        });
    }

    @Test
    public void start_actionIsPending_throwsIllegalArgumentException() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        mPendingActions.put(action, mock(RunningActionContext.class));

        assertThrows(IllegalArgumentException.class, ()-> {
            mScheduler.start(action);
        });
    }

    @Test
    public void cancel_actionIsRunning_cancelsAndEndsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        mRunningActions.put(action, context);

        mScheduler.cancel(action);

        verify(context, times(1)).markForCancellation();
        verify(context, times(1)).iterate();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void cancel_actionIsPending_removesAction() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        RunningActionContext context = mock(RunningActionContext.class);
        mPendingActions.put(action, context);

        mScheduler.cancel(action);

        assertThat(mPendingActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void cancel_actionNotRunning_throwsIllegalArgumentException() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();

        assertThrows(IllegalArgumentException.class, ()-> {
            mScheduler.cancel(action);
        });
    }

    @Test
    public void isRunning_actionNotRunning_returnsFalse() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();

        assertFalse(mScheduler.isRunning(action));
    }

    @Test
    public void isRunning_actionIsRunning_returnsTrue() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        mRunningActions.put(action, mock(RunningActionContext.class));

        assertTrue(mScheduler.isRunning(action));
    }

    @Test
    public void isRunning_actionIsPending_returnsTrue() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        mPendingActions.put(action, mock(RunningActionContext.class));

        assertTrue(mScheduler.isRunning(action));
    }

    @Test
    public void cancelActionsIf_predicateMatchesOnRunningAction_cancelsAndEndsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.getAction()).thenReturn(action);
        mRunningActions.put(action, context);

        mScheduler.cancelActionsIf((a)-> a.equals(action));

        verify(context, times(1)).markForCancellation();
        verify(context, times(1)).iterate();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void cancelActionsIf_predicateMatchesOnPendingAction_removesAction() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.getAction()).thenReturn(action);
        mPendingActions.put(action, context);

        mScheduler.cancelActionsIf((a)-> a.equals(action));

        assertThat(mPendingActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void cancelAllActions_hasRunningAction_cancelsAndEndsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.getAction()).thenReturn(action);
        mRunningActions.put(action, context);

        mScheduler.cancelAllActions();

        verify(context, times(1)).markForCancellation();
        verify(context, times(1)).iterate();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void cancelAllActions_hasPendingAction_removesAction() throws Exception {
        ActionInterface action = ActionsMock.actionMocker().build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.getAction()).thenReturn(action);
        mPendingActions.put(action, context);

        mScheduler.cancelAllActions();

        assertThat(mPendingActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void run_hasRunningActionNotFinishing_iteratesOnContextAndKeepsAction() {
        ActionInterface action = ActionsMock.actionMocker().build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.iterate()).thenReturn(false);
        mRunningActions.put(action, context);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        verify(context, times(1)).iterate();

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_hasRunningActionFinishing_iteratesOnContextAndRemovesAction() {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.iterate()).thenReturn(true);
        mRunningActions.put(action, context);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        verify(context, times(1)).iterate();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void run_hasRunningActionDisabledModeWhenShouldNot_cancelsIteratesOnActionAndRemoves() {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .mockRunWhenDisabled(false)
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.iterate()).thenReturn(true);
        mRunningActions.put(action, context);

        mScheduler.run(SchedulerModeMock.mockDisabledMode());

        verify(context, times(1)).markForCancellation();
        verify(context, times(1)).iterate();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void run_hasRunningActionDisabledModeWhenShould_iteratesOnActionAndKeeps() {
        Requirement requirement = mock(Requirement.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .mockRunWhenDisabled(true)
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        when(context.iterate()).thenReturn(false);
        when(context.shouldRunInDisabled()).thenReturn(true);
        mRunningActions.put(action, context);

        mScheduler.run(SchedulerModeMock.mockDisabledMode());

        verify(context, never()).markForCancellation();
        verify(context, times(1)).iterate();

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_hasDefaultActionAndCanRun_startsAction() {
        Subsystem subsystem = mock(Subsystem.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();
        mDefaultActions.put(subsystem, action);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(subsystem, action));
    }

    @Test
    public void run_hasDefaultActionAndCannotRun_doesNotStartAction() {
        Subsystem subsystem = mock(Subsystem.class);
        ActionInterface action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();
        RunningActionContext context = mock(RunningActionContext.class);
        mRunningActions.put(action, context);
        mRequirementsUsage.put(subsystem, action);

        ActionInterface defaultAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();
        mDefaultActions.put(subsystem, defaultAction);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
        assertThat(mRunningActions, not(IsMapContaining.hasKey(defaultAction)));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(subsystem, action));
    }

    @Test
    public void run_actionCallsStart_newActionStarts() throws Exception {
        ActionInterface actionToStart = ActionsMock.actionMocker()
                .mockIsFinished(false)
                .build();
        ActionInterface startingAction = new ActionBase(mScheduler) {
            @Override
            public void execute(ActionControl control) {
                mScheduler.start(actionToStart);
                control.finish();
            }
        };

        mScheduler.start(startingAction);
        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());
        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(actionToStart));
    }
}