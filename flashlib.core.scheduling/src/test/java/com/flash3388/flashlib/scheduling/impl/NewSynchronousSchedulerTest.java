package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.time.Clock;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class NewSynchronousSchedulerTest {

    private Map<Action, RunningActionContext> mPendingActions;
    private Map<Action, RunningActionContext> mRunningActions;
    private Map<Requirement, Action> mRequirementsUsage;
    private Map<Subsystem, Action> mDefaultActions;

    private NewSynchronousScheduler mScheduler;

    @BeforeEach
    public void setUp() throws Exception {
        mPendingActions = new HashMap<>();
        mRunningActions = new HashMap<>();
        mRequirementsUsage = new HashMap<>();
        mDefaultActions = new HashMap<>();

        mScheduler = new NewSynchronousScheduler(mock(Clock.class), mock(Logger.class),
                mPendingActions, mRunningActions, mRequirementsUsage, mDefaultActions);
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
    public void start_actionNotRunningHasConflicts_putsActionInPending() throws Exception {
        Requirement requirement = mock(Requirement.class);
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mRequirementsUsage.put(requirement, action);
        mRunningActions.put(action, mock(RunningActionContext.class));

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(requirement))
                .build();
        mScheduler.start(newAction);

        assertThat(mPendingActions, IsMapContaining.hasKey(newAction));
        assertThat(mRunningActions, not(IsMapContaining.hasKey(newAction)));
    }
}