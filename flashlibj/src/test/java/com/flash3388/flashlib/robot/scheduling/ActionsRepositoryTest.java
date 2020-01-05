package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockActionWithRequirement;
import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockActionWithoutRequirements;
import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockNotRunningAction;
import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockRunningAction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.mockito.Mockito.mock;

public class ActionsRepositoryTest {

    private Clock mClock;

    private Map<Subsystem, Action> mActionsOnSubsystems;
    private Map<Action, ActionContext> mRunningActions;
    private Collection<Action> mNextRunActions;

    private ActionsRepository mActionsRepository;

    @BeforeEach
    public void setup() throws Exception {
        mClock = mock(Clock.class);

        mActionsOnSubsystems = new HashMap<>();
        mRunningActions = new HashMap<>();
        mNextRunActions = new ArrayList<>();

        mActionsRepository = new ActionsRepository(
                mActionsOnSubsystems, new HashMap<>(),
                mRunningActions, mNextRunActions,
                mClock, mock(Logger.class));
    }

    @Test
    public void addAction_forSomeAction_addsActionForFutureRun() throws Exception {
        Action action = mock(Action.class);

        mActionsRepository.addAction(action);

        assertThat(mNextRunActions, hasItems(action));
    }

    @Test
    public void updateActionsForNextRun_withActionsScheduledForNextRun_startsThoseActions() throws Exception {
        Collection<Action> actions = Arrays.asList(
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements()
        );
        mNextRunActions.addAll(actions);

        mActionsRepository.updateActionsForNextRun(Collections.emptyList());

        assertThat(mRunningActions, mapHasKeys(actions));
        assertThat(mNextRunActions, emptyIterable());
    }

    @Test
    public void updateActionsForNextRun_withActionsToRemove_removesThoseActionsKeepsTheRest() throws Exception {
        Collection<Action> removeActions = Arrays.asList(
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements()
        );
        Collection<Action> keepActions = Arrays.asList(
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements(),
                mockActionWithoutRequirements()
        );

        removeActions.forEach((action) -> mRunningActions.put(action, contextForAction(action)));
        keepActions.forEach((action) -> mRunningActions.put(action, contextForAction(action)));

        mActionsRepository.updateActionsForNextRun(removeActions);

        assertThat(mRunningActions, not(mapHasKeys(removeActions)));
        assertThat(mRunningActions, mapHasKeys(keepActions));
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
        mRunningActions.put(action, contextForAction(action));

        mActionsRepository.updateActionsForNextRun(Collections.singletonList(action));

        assertThat(mActionsOnSubsystems, not(hasEntry(subsystem, action)));
    }

    @Test
    public void removeActionsIf_forRunningActions_removesAllRunning() throws Exception {
        Collection<Action> toBeRemovedActions = Arrays.asList(
                mockRunningAction(),
                mockRunningAction(),
                mockRunningAction(),
                mockRunningAction()
        );
        Collection<Action> toKeepActions = Arrays.asList(
                mockNotRunningAction(),
                mockNotRunningAction()
        );

        toBeRemovedActions.forEach((action) -> mRunningActions.put(action, contextForAction(action)));
        mNextRunActions.addAll(toKeepActions);

        mActionsRepository.removeActionsIf(Action::isRunning);

        assertThat(mRunningActions, anEmptyMap());
        assertThat(mNextRunActions, hasItems(toKeepActions.toArray(new Action[0])));
    }

    @Test
    public void removeActionsIf_forActionsWithSpecificRequirements_removesSpecificActions() throws Exception {
        Subsystem toRemove = mock(Subsystem.class);
        Subsystem toKeep = mock(Subsystem.class);

        Collection<Action> toKeepRunningActions = Arrays.asList(
                mockActionWithRequirement(toKeep),
                mockActionWithRequirement(toKeep)
        );
        Collection<Action> toKeepNextRunActions = Arrays.asList(
                mockActionWithRequirement(toKeep),
                mockActionWithRequirement(toKeep)
        );

        Arrays.asList(
                mockActionWithRequirement(toRemove),
                mockActionWithRequirement(toRemove)
        ).forEach((action) -> mRunningActions.put(action, contextForAction(action)));
        toKeepRunningActions.forEach((action) -> mRunningActions.put(action, contextForAction(action)));

        mNextRunActions.addAll(Arrays.asList(
                mockActionWithRequirement(toRemove),
                mockActionWithRequirement(toRemove)
        ));
        mNextRunActions.addAll(toKeepNextRunActions);

        mActionsRepository.removeActionsIf((action) -> action.getRequirements().contains(toRemove));

        assertThat(mRunningActions, mapHasKeys(toKeepRunningActions));
        assertThat(mRunningActions, aMapWithSize(toKeepRunningActions.size()));

        assertThat(mNextRunActions, hasItems(toKeepNextRunActions.toArray(new Action[0])));
        assertThat(mNextRunActions, hasSize(toKeepNextRunActions.size()));
    }

    private <T> Matcher<Map<T, ?>> mapHasKeys(Collection<T> keys) {
        return allOf(keys.stream()
                .map(Matchers::hasKey)
                .collect(Collectors.toList()));
    }

    private ActionContext contextForAction(Action action) {
        return new ActionContext(action, mClock);
    }
}