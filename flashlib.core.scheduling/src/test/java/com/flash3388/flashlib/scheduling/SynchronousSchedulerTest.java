package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.scheduling.actions.SynchronousActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SynchronousSchedulerTest {

    private Clock mClock;
    private Logger mLogger;
    private Map<Action, SynchronousActionContext> mActionsContexts;
    private Map<Requirement, Action> mRunningOnRequirements;
    private Map<Subsystem, Action> mDefaultActions;

    @BeforeEach
    public void setUp() throws Exception {
        mClock = mock(Clock.class);
        mLogger = mock(Logger.class);
        mActionsContexts = new HashMap<>();
        mRunningOnRequirements = new HashMap<>();
        mDefaultActions = new HashMap<>();
    }

    @Test
    public void start_withAction_actionIsSavedContextInitialized() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.start(action);

        assertThat(mActionsContexts, hasEntry(equalTo(action), notNullValue()));
        SynchronousActionContext context = mActionsContexts.get(action);
        assertTrue(context.isStarted());
    }

    @Test
    public void start_actionAlreadyRunning_throwsIllegalStateException() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .build();
        mActionsContexts.put(action, context);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);

        assertThrows(IllegalStateException.class, ()-> {
            scheduler.start(action);
        });
    }

    @Test
    public void start_withActionAndRequirements_updatesRequirementsWithNewAction() throws Exception {
        Collection<Requirement> requirements = Arrays.asList(mock(Requirement.class), mock(Requirement.class));
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(requirements)
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.start(action);

        for (Requirement requirement : requirements) {
            assertThat(mRunningOnRequirements, hasEntry(requirement, action));
        }
    }

    @Test
    public void start_withActionAndRequirements_cancelsOldRequirements() throws Exception {
        Collection<Requirement> requirements = Arrays.asList(mock(Requirement.class), mock(Requirement.class));
        Collection<SynchronousActionContext> oldContexts = new ArrayList<>();
        for (Requirement requirement : requirements) {
            Action old = ActionsMock.actionMocker()
                    .mockWithRequirements(Collections.singleton(requirement))
                    .build();
            SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(old)
                    .mockRunning(true)
                    .build();
            oldContexts.add(context);
            mRunningOnRequirements.put(requirement, old);
            mActionsContexts.put(old, context);
        }

        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(requirements)
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.start(action);

        for (SynchronousActionContext context : oldContexts) {
            verify(context, times(1)).cancelAndFinish();
        }
    }

    @Test
    public void cancel_actionRunning_cancelsAndRemoves() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .build();
        mActionsContexts.put(action, context);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.cancel(action);

        verify(context, times(1)).cancelAndFinish();
        assertThat(mActionsContexts, not(hasEntry(action, context)));
    }

    @Test
    public void cancel_actionNotRunning_throwsIllegalStateException() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);

        assertThrows(IllegalStateException.class, ()-> {
            scheduler.cancel(action);
        });
    }

    @Test
    public void cancel_actionRunningWithRequirements_updatesRequirementTable() throws Exception {
        Collection<Requirement> requirements = Arrays.asList(mock(Requirement.class), mock(Requirement.class));
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(requirements)
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .build();
        mActionsContexts.put(action, context);
        requirements.forEach((requirement)->mRunningOnRequirements.put(requirement, action));

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.cancel(action);

        for (Requirement requirement : requirements) {
            assertThat(mRunningOnRequirements, not(hasEntry(requirement, action)));
        }
    }

    @Test
    public void isRunning_actionIsRunning_returnsTrue() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .build();
        mActionsContexts.put(action, context);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        boolean isRunning = scheduler.isRunning(action);
        assertTrue(isRunning);
    }

    @Test
    public void isRunning_actionNotRunning_returnsFalse() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        boolean isRunning = scheduler.isRunning(action);
        assertFalse(isRunning);
    }

    @Test
    public void getActionRunTime_actionRunning_returnsTime() throws Exception {
        Time runTime = Time.milliseconds(5);

        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunTime(runTime)
                .build();
        mActionsContexts.put(action, context);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        Time time = scheduler.getActionRunTime(action);

        assertThat(time, equalTo(runTime));
    }

    @Test
    public void getActionRunTime_actionNotRunning_throwsIllegalStateException() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        assertThrows(IllegalStateException.class, ()-> {
            scheduler.getActionRunTime(action);
        });
    }

    @Test
    public void cancelActionsIf_forSpecificAction_cancelsAction() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .build();
        mActionsContexts.put(action, context);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.cancelActionsIf((action1)-> action1.equals(action));

        verify(context, times(1)).cancelAndFinish();
        assertThat(mActionsContexts, not(hasEntry(action, context)));
    }

    @Test
    public void cancelActionsIf_forSeveralActions_cancelsOnlyMatchedActions() throws Exception {
        Map<Action, SynchronousActionContext> matching = new HashMap<>();
        Map<Action, SynchronousActionContext> notMatching = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            Action action = ActionsMock.actionMocker()
                    .build();
            SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                    .mockRunning(true)
                    .build();
            matching.put(action, context);
        }
        for (int i = 0; i < 2; i++) {
            Action action = ActionsMock.actionMocker()
                    .build();
            SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                    .mockRunning(true)
                    .build();
            notMatching.put(action, context);
        }

        mActionsContexts.putAll(matching);
        mActionsContexts.putAll(notMatching);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.cancelActionsIf(matching::containsKey);

        for (Map.Entry<Action, SynchronousActionContext> entry : matching.entrySet()) {
            verify(entry.getValue(), times(1)).cancelAndFinish();
            assertThat(mActionsContexts, not(hasEntry(entry.getKey(), entry.getValue())));
        }
        for (Map.Entry<Action, SynchronousActionContext> entry : notMatching.entrySet()) {
            verify(entry.getValue(), never()).cancelAndFinish();
            assertThat(mActionsContexts, hasEntry(entry.getKey(), entry.getValue()));
        }
    }

    @Test
    public void cancelAllActions_hasSeveralActions_cancelsAll() throws Exception {
        Map<Action, SynchronousActionContext> all = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            Action action = ActionsMock.actionMocker()
                    .build();
            SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                    .mockRunning(true)
                    .build();
            all.put(action, context);
        }

        mActionsContexts.putAll(all);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.cancelAllActions();

        for (Map.Entry<Action, SynchronousActionContext> entry : all.entrySet()) {
            verify(entry.getValue(), times(1)).cancelAndFinish();
            assertThat(mActionsContexts, not(hasEntry(entry.getKey(), entry.getValue())));
        }
    }

    @Test
    public void setDefaultAction_noPreviousDefault_updatesTable() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.setDefaultAction(subsystem, action);

        assertThat(mDefaultActions, hasEntry(subsystem, action));
    }

    @Test
    public void setDefaultAction_actionMissingRequirement_throwsIllegalArgumentException() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        assertThrows(IllegalArgumentException.class, ()-> {
            scheduler.setDefaultAction(subsystem, action);
        });
    }

    @Test
    public void setDefaultAction_hasPreviousDefault_replacesIt() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action old = ActionsMock.actionMocker()
                .build();
        mDefaultActions.put(subsystem, old);

        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.setDefaultAction(subsystem, action);

        assertThat(mDefaultActions, hasEntry(subsystem, action));
    }

    @Test
    public void setDefaultAction_hasPreviousDefaultRunning_replacesAndCancelsIt() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action old = ActionsMock.actionMocker()
                .build();
        SynchronousActionContext oldContext = ActionsMock.synchronousActionContextMocker(old)
                .mockRunning(true)
                .build();
        mDefaultActions.put(subsystem, old);
        mActionsContexts.put(old, oldContext);

        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.setDefaultAction(subsystem, action);

        verify(oldContext, times(1)).cancelAndFinish();
        assertThat(mActionsContexts, not(hasEntry(old, oldContext)));
        assertThat(mDefaultActions, hasEntry(subsystem, action));
    }

    @Test
    public void getActionRunningOnRequirement_hasActionRunning_returnsAction() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .build();
        mRunningOnRequirements.put(requirement, action);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        Optional<Action> optional = scheduler.getActionRunningOnRequirement(requirement);

        assertTrue(optional.isPresent());
        assertThat(optional.get(), equalTo(action));
    }

    @Test
    public void getActionRunningOnRequirement_hasNoAction_returnsEmptyOptional() throws Exception {
        Requirement requirement = mock(Requirement.class);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        Optional<Action> optional = scheduler.getActionRunningOnRequirement(requirement);

        assertFalse(optional.isPresent());
    }

    @Test
    public void run_hasActionsRunning_runsActions() throws Exception {
        Map<Action, SynchronousActionContext> all = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            Action action = ActionsMock.actionMocker()
                    .build();
            SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                    .mockRunning(true)
                    .mockNextRunFinished(false)
                    .build();
            all.put(action, context);
        }

        mActionsContexts.putAll(all);

        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(false);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.run(mode);

        for (Map.Entry<Action, SynchronousActionContext> entry : all.entrySet()) {
            verify(entry.getValue(), times(1)).run();
        }
    }

    @Test
    public void run_hasActionFinished_removesActionsAndUpdatesRequirements() throws Exception {
        Collection<Requirement> requirements = Arrays.asList(mock(Requirement.class), mock(Requirement.class));
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(requirements)
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .mockNextRunFinished(true)
                .build();

        mActionsContexts.put(action, context);
        requirements.forEach((req)-> mRunningOnRequirements.put(req, action));

        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(false);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.run(mode);

        assertThat(mActionsContexts, not(hasEntry(action, context)));
        for (Requirement requirement : requirements) {
            assertThat(mRunningOnRequirements, not(hasEntry(requirement, action)));
        }
    }

    @Test
    public void run_disabledModeAndActionShouldStop_cancelsActionAndRemoves() throws Exception {
        Action action = ActionsMock.actionMocker()
                .mockRunWhenDisabled(false)
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .mockNextRunFinished(true)
                .build();

        mActionsContexts.put(action, context);

        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(true);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.run(mode);

        verify(context, times(1)).cancelAndFinish();
        assertThat(mActionsContexts, not(hasEntry(action, context)));
    }

    @Test
    public void run_canStartDefaultActions_startsThem() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();
        mDefaultActions.put(subsystem, action);

        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(false);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.run(mode);

        assertThat(mActionsContexts, hasKey(action));
        assertThat(mRunningOnRequirements, hasEntry(subsystem, action));
    }

    @Test
    public void run_cannotStartDefaultActions_doesNotStartThem() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();
        mDefaultActions.put(subsystem, action);

        Action old = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();
        SynchronousActionContext context = ActionsMock.synchronousActionContextMocker(action)
                .mockRunning(true)
                .mockNextRunFinished(false)
                .build();
        mActionsContexts.put(old, context);
        mRunningOnRequirements.put(subsystem, old);

        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(false);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.run(mode);

        assertThat(mActionsContexts, not(hasKey(action)));
        assertThat(mRunningOnRequirements, hasEntry(subsystem, old));
    }

    @Test
    public void run_isDisabledAndHasDefaults_doesNotStartThem() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystem)
                .build();
        mDefaultActions.put(subsystem, action);

        SchedulerMode mode = mock(SchedulerMode.class);
        when(mode.isDisabled()).thenReturn(true);

        Scheduler scheduler = new SynchronousScheduler(mClock, mLogger, mActionsContexts, mRunningOnRequirements, mDefaultActions);
        scheduler.run(mode);

        assertThat(mActionsContexts, not(hasKey(action)));
        assertThat(mRunningOnRequirements, not(hasEntry(subsystem, action)));
    }
}