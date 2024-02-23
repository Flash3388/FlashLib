package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.SchedulerModeMock;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.scheduling.impl.triggers.GenericTrigger;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionController;
import com.flash3388.flashlib.time.ClockMock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SingleThreadedSchedulerTest {

    private Map<Action, ExecutionContext> mPendingActions;
    private Map<Action, ExecutionContext> mRunningActions;
    private Collection<GenericTrigger> mTriggers;
    private Map<Requirement, Action> mRequirementsUsage;
    private Map<Subsystem, RegisteredDefaultAction> mDefaultActions;

    private SingleThreadedScheduler mScheduler;

    @BeforeEach
    public void setUp() throws Exception {
        mPendingActions = new HashMap<>();
        mRunningActions = new HashMap<>();
        mTriggers = new ArrayList<>();
        mRequirementsUsage = new HashMap<>();
        mDefaultActions = new HashMap<>();

        mScheduler = new SingleThreadedScheduler(
                ClockMock.mockInvalidTimeClock(),
                new FlashLibMainThread.Stub(),
                new StoredObject.Stub(),
                mPendingActions,
                mRunningActions,
                new ArrayList<>(),
                mTriggers,
                mRequirementsUsage,
                mDefaultActions);
    }

    @Test
    public void start_actionNotRunningNoConflicts_putsActionInRunningAndUpdatesRequirements() throws Exception {
        Requirement requirement = mock(Requirement.class);

        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(action);

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(requirement, action));
    }

    @Test
    public void start_actionNotRunningHasNonPreferredConflicts_cancelsAndEndsConflictingStartsNew() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mRequirementsUsage.put(requirement, action);
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mRunningActions.put(action, executionContext);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(newAction);

        verify(executionContext, times(1)).interrupt();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRunningActions, IsMapContaining.hasKey(newAction));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(requirement, newAction));
    }

    @Test
    public void start_actionNotRunningHasConflicts_marksConflictingForCancellation() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mRequirementsUsage.put(requirement, action);
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithRequirements(requirement)
                .build();
        mRunningActions.put(action, executionContext);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(newAction);

        verify(executionContext, times(1)).interrupt();
    }

    @Test
    public void start_actionIsRunning_throwsIllegalArgumentException() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        mRunningActions.put(action, mock(ExecutionContext.class));

        assertThrows(IllegalArgumentException.class, ()-> {
            mScheduler.start(action);
        });
    }

    @Test
    public void start_actionIsPending_throwsIllegalArgumentException() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        mPendingActions.put(action, mock(ExecutionContext.class));

        assertThrows(IllegalArgumentException.class, ()-> {
            mScheduler.start(action);
        });
    }

    @Test
    public void cancel_actionIsRunning_cancelsAndEndsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithRequirements(requirement)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.cancel(action);

        verify(executionContext, times(1)).interrupt();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void cancel_actionIsPending_removesAction() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mPendingActions.put(action, executionContext);

        mScheduler.cancel(action);

        assertThat(mPendingActions, not(IsMapContaining.hasKey(action)));
        verify(executionContext, times(1)).interrupt();
    }

    @Test
    public void cancel_actionNotRunning_throwsIllegalArgumentException() throws Exception {
        Action action = ActionsMock.actionMocker().build();

        assertThrows(IllegalArgumentException.class, ()-> {
            mScheduler.cancel(action);
        });
    }

    @Test
    public void isRunning_actionNotRunning_returnsFalse() throws Exception {
        Action action = ActionsMock.actionMocker().build();

        assertFalse(mScheduler.isRunning(action));
    }

    @Test
    public void isRunning_actionIsRunning_returnsTrue() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action).build();
        mRunningActions.put(action, executionContext);

        assertTrue(mScheduler.isRunning(action));
    }

    @Test
    public void isRunning_actionIsPending_returnsTrue() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        mPendingActions.put(action, mock(ExecutionContext.class));

        assertTrue(mScheduler.isRunning(action));
    }

    @Test
    public void cancelActionsIf_predicateMatchesOnRunningAction_cancelsAndEndsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithRequirements(requirement)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.cancelActionsIf((a)-> a.equals(action));

        verify(executionContext, times(1)).interrupt();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void cancelActionsIf_predicateMatchesOnPendingAction_removesAction() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mPendingActions.put(action, executionContext);

        mScheduler.cancelActionsIf((a)-> a.equals(action));

        assertThat(mPendingActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void cancelAllActions_hasRunningAction_cancelsAndEndsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithRequirements(requirement)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.cancelAllActions();

        verify(executionContext, times(1)).interrupt();

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void cancelAllActions_hasPendingAction_removesAction() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mPendingActions.put(action, executionContext);

        mScheduler.cancelAllActions();

        assertThat(mPendingActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void run_hasRunningActionNotFinishing_iteratesOnContextAndKeepsAction() {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        verify(executionContext, times(1)).execute(any(SchedulerMode.class));

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_hasRunningActionFinishing_iteratesOnContextAndRemovesAction() {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithResult(ExecutionContext.ExecutionResult.FINISHED)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        verify(executionContext, times(1)).execute(any(SchedulerMode.class));

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void run_hasRunningActionButFinished_removed() {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithRequirements(requirement)
                .mockWithResult(ExecutionContext.ExecutionResult.FINISHED)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.run(SchedulerModeMock.mockDisabledMode());

        verify(executionContext, times(1)).execute(any(SchedulerMode.class));

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
        assertThat(mRequirementsUsage, not(IsMapContaining.hasKey(requirement)));
    }

    @Test
    public void run_hasRunningActionDisabledModeWhenShould_iteratesOnActionAndKeeps() {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .mockRunWhenDisabled(true)
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockShouldRunInDisabled(true)
                .build();
        mRunningActions.put(action, executionContext);

        mScheduler.run(SchedulerModeMock.mockDisabledMode());

        verify(executionContext, never()).interrupt();
        verify(executionContext, times(1)).execute(any(SchedulerMode.class));

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_hasDefaultActionAndCanRun_startsAction() {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();
        RegisteredDefaultAction registeredDefaultAction = new RegisteredDefaultAction(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                subsystem);
        mDefaultActions.put(subsystem, registeredDefaultAction);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(subsystem, action));
    }

    @Test
    public void run_hasDefaultActionAndCannotRun_doesNotStartAction() {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .mockWithRequirements(subsystem)
                .build();
        mRunningActions.put(action, executionContext);
        mRequirementsUsage.put(subsystem, action);

        Action defaultAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();
        RegisteredDefaultAction registeredDefaultAction = new RegisteredDefaultAction(
                0,
                action,
                action.getConfiguration(),
                mock(ObsrActionContext.class),
                subsystem);
        mDefaultActions.put(subsystem, registeredDefaultAction);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
        assertThat(mRunningActions, not(IsMapContaining.hasKey(defaultAction)));
        assertThat(mRequirementsUsage, IsMapContaining.hasEntry(subsystem, action));
    }

    @Test
    public void run_actionCallsStart_newActionStarts() throws Exception {
        Action actionToStart = ActionsMock.actionMocker()
                .mockIsFinished(false)
                .build();
        Action startingAction = new ActionBase(mScheduler) {
            @Override
            public void initialize(ActionControl control) {}

            @Override
            public void execute(ActionControl control) {
                mScheduler.start(actionToStart);
                control.finish();
            }

            @Override
            public void end(FinishReason reason) {}
        };

        mScheduler.start(startingAction);
        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());
        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(actionToStart));
    }

    @Test
    public void run_withTriggersWithActionNotRunning_startsActionFromTrigger() throws Exception {
        Action action = ActionsMock.actionMocker().build();

        GenericTrigger trigger = new GenericTrigger() {
            @Override
            public void update(TriggerActionController controller) {
                controller.addActionToStartIfNotRunning(action);
            }
        };
        mTriggers.add(trigger);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_withTriggersWithActionRunning_notStartsActionFromTrigger() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mRunningActions.put(action, executionContext);

        GenericTrigger trigger = new GenericTrigger() {
            @Override
            public void update(TriggerActionController controller) {
                controller.addActionToStartIfNotRunning(action);
            }
        };
        mTriggers.add(trigger);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_withTriggersAndActionRunning_stopsActionFromTrigger() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                .build();
        mRunningActions.put(action, executionContext);

        GenericTrigger trigger = new GenericTrigger() {
            @Override
            public void update(TriggerActionController controller) {
                controller.addActionToStopIfRunning(action);
            }
        };
        mTriggers.add(trigger);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void run_withTriggersAndActionNotRunning_notStopsActionFromTrigger() throws Exception {
        Action action = ActionsMock.actionMocker().build();

        GenericTrigger trigger = new GenericTrigger() {
            @Override
            public void update(TriggerActionController controller) {
                controller.addActionToStopIfRunning(action);
            }
        };
        mTriggers.add(trigger);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
    }

    @Test
    public void run_withTriggersAndActionNotRunning_toggleToStartActionFromTrigger() throws Exception {
        Action action = ActionsMock.actionMocker().build();

        GenericTrigger trigger = new GenericTrigger() {
            @Override
            public void update(TriggerActionController controller) {
                controller.addActionToToggle(action);
            }
        };
        mTriggers.add(trigger);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, IsMapContaining.hasKey(action));
    }

    @Test
    public void run_withTriggersAndActionRunning_toggleToStopActionFromTrigger() throws Exception {
        Action action = ActionsMock.actionMocker().build();
        ExecutionContext executionContext = ActionsMock.executionContextMocker(action)
                        .build();
        mRunningActions.put(action, executionContext);

        GenericTrigger trigger = new GenericTrigger() {
            @Override
            public void update(TriggerActionController controller) {
                controller.addActionToToggle(action);
            }
        };
        mTriggers.add(trigger);

        mScheduler.run(SchedulerModeMock.mockNotDisabledMode());

        assertThat(mRunningActions, not(IsMapContaining.hasKey(action)));
    }
}