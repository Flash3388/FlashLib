package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.util.logging.Logging;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SubsystemControlTest {

    private Map<Subsystem, Action> mActionsOnSubsystems;
    private Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    private SubsystemControl mSubsystemControl;

    @BeforeEach
    public void setup() throws Exception {
        mActionsOnSubsystems = new HashMap<>();
        mDefaultActionsOnSubsystems = new HashMap<>();
        mSubsystemControl = new SubsystemControl(Logging.stub(), mActionsOnSubsystems, mDefaultActionsOnSubsystems);
    }

    @Test
    public void updateRequirementsNoCurrentAction_actionUsesAllSubsystems_removesAllSubsystems() throws Exception {
        Collection<Subsystem> subsystems = Arrays.asList(
                mock(Subsystem.class),
                mock(Subsystem.class),
                mock(Subsystem.class));
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(subsystems)
                .build();

        subsystems.forEach((s) -> mActionsOnSubsystems.put(s, action));

        mSubsystemControl.updateRequirementsNoCurrentAction(action);

        assertThat(mActionsOnSubsystems, IsMapWithSize.anEmptyMap());
    }

    @Test
    public void updateRequirementsNoCurrentAction_actionUsesSome_removesOnlyMatchingSubsystems() throws Exception {
        Collection<Subsystem> otherSubsystems = Arrays.asList(
                mock(Subsystem.class),
                mock(Subsystem.class),
                mock(Subsystem.class));
        Collection<Subsystem> usedSubsystems = Arrays.asList(
                mock(Subsystem.class),
                mock(Subsystem.class),
                mock(Subsystem.class));
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(usedSubsystems)
                .build();

        otherSubsystems.forEach((s) -> mActionsOnSubsystems.put(s, mock(Action.class)));
        usedSubsystems.forEach((s) -> mActionsOnSubsystems.put(s, action));

        mSubsystemControl.updateRequirementsNoCurrentAction(action);

        assertThat(mActionsOnSubsystems.keySet(),
                IsIterableWithSize.iterableWithSize(otherSubsystems.size()));
        assertThat(mActionsOnSubsystems.keySet(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(otherSubsystems.toArray()));
    }

    @Test
    public void updateRequirementsWithNewRunningAction_actionWithSomeRequirements_addsRequirementsToMap() throws Exception {
        Collection<Subsystem> otherSubsystems = Arrays.asList(
                mock(Subsystem.class),
                mock(Subsystem.class),
                mock(Subsystem.class));
        Collection<Subsystem> usedSubsystems = Arrays.asList(
                mock(Subsystem.class),
                mock(Subsystem.class),
                mock(Subsystem.class));
        Action action = ActionsMock.actionMocker()
                .mockWithRequirements(usedSubsystems)
                .build();

        otherSubsystems.forEach((s) -> mActionsOnSubsystems.put(s, mock(Action.class)));

        mSubsystemControl.updateRequirementsWithNewRunningAction(action);

        assertThat(mActionsOnSubsystems, IsMapWithSize.aMapWithSize(usedSubsystems.size() + otherSubsystems.size()));
        otherSubsystems.forEach((s)-> assertThat(mActionsOnSubsystems,
                IsMapContaining.hasEntry(equalTo(s), any(Action.class))));

        usedSubsystems.forEach((s)->
                assertThat(mActionsOnSubsystems, IsMapContaining.hasEntry(s, action)));
    }

    @Test
    public void updateRequirementsWithNewRunningAction_requirementHasAction_cancelsAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);
        mActionsOnSubsystems.put(subsystem, action);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();

        mSubsystemControl.updateRequirementsWithNewRunningAction(newAction);

        verify(action, times(1)).cancel();
    }

    @Test
    public void updateRequirementsWithNewRunningAction_requirementHasAction_replacesAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);
        mActionsOnSubsystems.put(subsystem, action);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();

        mSubsystemControl.updateRequirementsWithNewRunningAction(newAction);

        assertThat(mActionsOnSubsystems, Matchers.not(IsMapContaining.hasEntry(action, subsystem)));
    }

    @Test
    public void getActionOnSubsystem_forSubsystemWithAction_returnsAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);
        mActionsOnSubsystems.put(subsystem, action);

        Optional<Action> optionalAction = mSubsystemControl.getActionOnSubsystem(subsystem);
        assertThat(optionalAction.get(), equalTo(action));
    }

    @Test
    public void getActionOnSubsystem_subsystemHasNoAction_returnsEmpty() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);

        Optional<Action> optionalAction = mSubsystemControl.getActionOnSubsystem(subsystem);
        assertFalse(optionalAction.isPresent());
    }

    @Test
    public void setDefaultActionOnSubsystem_defaultAlreadyExists_replacesAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);
        mDefaultActionsOnSubsystems.put(subsystem, action);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();

        mSubsystemControl.setDefaultActionOnSubsystem(subsystem, newAction);

        assertThat(mDefaultActionsOnSubsystems, IsMapContaining.hasEntry(subsystem, newAction));
    }

    @Test
    public void setDefaultActionOnSubsystem_defaultNotExists_putAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();

        mSubsystemControl.setDefaultActionOnSubsystem(subsystem, newAction);

        assertThat(mDefaultActionsOnSubsystems, IsMapContaining.hasEntry(subsystem, newAction));
    }

    @Test
    public void setDefaultActionOnSubsystem_subsystemNotInRequirements_subsystemAdded() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action newAction = ActionsMock.actionMocker().build();

        mSubsystemControl.setDefaultActionOnSubsystem(subsystem, newAction);

        assertThat(newAction.getConfiguration().getRequirements(), IsIterableContaining.hasItem(subsystem));
    }

    @Test
    public void getDefaultActionsToStart_noActionsAreRunning_returnsAll() throws Exception {
        Map<Subsystem, Action> defaultActions = new HashMap<>();
        defaultActions.put(mock(Subsystem.class), mock(Action.class));
        defaultActions.put(mock(Subsystem.class), mock(Action.class));

        mDefaultActionsOnSubsystems.putAll(defaultActions);

        Map<Subsystem, Action> defaultActionsToStart = mSubsystemControl.getDefaultActionsToStart();

        assertThat(defaultActionsToStart, IsMapWithSize.aMapWithSize(defaultActions.size()));
        defaultActions.forEach((s, a) -> assertThat(defaultActionsToStart, IsMapContaining.hasEntry(s, a)));
    }

    @Test
    public void getDefaultActionsToStart_someActionsRunning_returnsTheOnesNotRunning() throws Exception {
        Map<Subsystem, Action> notRunning = new HashMap<>();
        notRunning.put(mock(Subsystem.class), mock(Action.class));
        notRunning.put(mock(Subsystem.class), mock(Action.class));

        Map<Subsystem, Action> running = new HashMap<>();
        running.put(mock(Subsystem.class), mock(Action.class));
        running.put(mock(Subsystem.class), mock(Action.class));

        mDefaultActionsOnSubsystems.putAll(notRunning);
        mDefaultActionsOnSubsystems.putAll(running);

        mActionsOnSubsystems.putAll(running);

        Map<Subsystem, Action> defaultActionsToStart = mSubsystemControl.getDefaultActionsToStart();

        assertThat(defaultActionsToStart, IsMapWithSize.aMapWithSize(notRunning.size()));
        notRunning.forEach((s, a) -> assertThat(defaultActionsToStart, IsMapContaining.hasEntry(s, a)));
    }
}