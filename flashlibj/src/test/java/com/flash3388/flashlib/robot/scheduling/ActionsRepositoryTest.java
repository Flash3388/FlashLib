package com.flash3388.flashlib.robot.scheduling;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockActionWithRequirement;
import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockActionWithoutRequirements;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ActionsRepositoryTest {

    private Map<Subsystem, Action> mActionsOnSubsystems;
    private Map<Subsystem, Action> mDefaultActionsOnSubsystems;
    private Collection<Action> mRunningActions;
    private Collection<Action> mNextRunActions;

    private ActionsRepository mActionsRepository;

    @Before
    public void setup() throws Exception {
        mActionsOnSubsystems = new HashMap<>();
        mDefaultActionsOnSubsystems = new HashMap<>();
        mRunningActions = new ArrayList<>();
        mNextRunActions = new ArrayList<>();

        mActionsRepository = new ActionsRepository(mActionsOnSubsystems, mDefaultActionsOnSubsystems, mRunningActions, mNextRunActions, mock(Logger.class));
    }

    @Test
    public void addAction_forSomeAction_addsActionForFutureRun() throws Exception {
        Action action = mock(Action.class);

        mActionsRepository.addAction(action);

        assertThat(mNextRunActions, hasItems(action));
    }

    @Test
    public void updateActionsForNextRun_withActionsScheduledForNextRun_startsThoseActions() throws Exception {
        Action[] actions = {
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements()
        };
        mNextRunActions.addAll(Arrays.asList(actions));

        mActionsRepository.updateActionsForNextRun(Collections.emptyList());

        assertThat(mRunningActions, hasItems(actions));
        assertThat(mNextRunActions, emptyIterable());
    }

    @Test
    public void updateActionsForNextRun_withActionsToRemove_removesThoseActionsKeepsTheRest() throws Exception {
        Action[] removeActions = {
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements()
        };
        Action[] keepActions = {
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements()
        };
        mRunningActions.addAll(Arrays.asList(removeActions));
        mRunningActions.addAll(Arrays.asList(keepActions));

        mActionsRepository.updateActionsForNextRun(Arrays.asList(removeActions));

        assertThat(mRunningActions, hasItems(keepActions));
        assertThat(mRunningActions, not(hasItems(removeActions)));
    }

    @Test
    public void updateActionsForNextRun_actionsForNextRunWithRequirements_updatesRequirementsWithRunningActions() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);

        Action action = mockActionWithRequirement(subsystem);
        mNextRunActions.addAll(Collections.singletonList(action));

        mActionsRepository.updateActionsForNextRun(Collections.emptyList());

        assertThat(mActionsOnSubsystems, hasEntry(subsystem, action));
    }

    @Test
    public void updateActionsForNextRun_actionRunningWithRequirements_updatesRequirementsWithNotRunningActions() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mockActionWithRequirement(subsystem);

        mActionsOnSubsystems.put(subsystem, action);
        mRunningActions.addAll(Collections.singletonList(action));

        mActionsRepository.updateActionsForNextRun(Collections.singletonList(action));

        assertThat(mActionsOnSubsystems, not(hasEntry(subsystem, action)));
    }
}