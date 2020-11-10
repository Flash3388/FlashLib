package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.util.logging.Logging;
import org.hamcrest.MatcherAssert;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RequirementsControlTest {

    private Map<Requirement, Action> mActionsOnSubsystems;
    private Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    private RequirementsControl mRequirementsControl;

    @BeforeEach
    public void setup() throws Exception {
        mActionsOnSubsystems = new HashMap<>();
        mDefaultActionsOnSubsystems = new HashMap<>();
        mRequirementsControl = new RequirementsControl(Logging.stub(), mActionsOnSubsystems, mDefaultActionsOnSubsystems);
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

        mRequirementsControl.updateRequirementsNoCurrentAction(action);

        MatcherAssert.assertThat(mActionsOnSubsystems, IsMapWithSize.anEmptyMap());
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

        mRequirementsControl.updateRequirementsNoCurrentAction(action);

        MatcherAssert.assertThat(mActionsOnSubsystems.keySet(),
                IsIterableWithSize.iterableWithSize(otherSubsystems.size()));
        MatcherAssert.assertThat(mActionsOnSubsystems.keySet(),
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

        mRequirementsControl.updateRequirementsWithNewRunningAction(action);

        MatcherAssert.assertThat(mActionsOnSubsystems, IsMapWithSize.aMapWithSize(usedSubsystems.size() + otherSubsystems.size()));
        otherSubsystems.forEach((s)-> MatcherAssert.assertThat(mActionsOnSubsystems,
                IsMapContaining.hasEntry(equalTo(s), any(Action.class))));

        usedSubsystems.forEach((s)->
                MatcherAssert.assertThat(mActionsOnSubsystems, IsMapContaining.hasEntry(s, action)));
    }

    @Test
    public void updateRequirementsWithNewRunningAction_requirementHasAction_cancelsAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);
        mActionsOnSubsystems.put(subsystem, action);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();

        mRequirementsControl.updateRequirementsWithNewRunningAction(newAction);

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

        mRequirementsControl.updateRequirementsWithNewRunningAction(newAction);

        MatcherAssert.assertThat(mActionsOnSubsystems, Matchers.not(IsMapContaining.hasEntry(action, subsystem)));
    }

    @Test
    public void getActionOnSubsystem_forSubsystemWithAction_returnsAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);
        mActionsOnSubsystems.put(subsystem, action);

        Optional<Action> optionalAction = mRequirementsControl.getActionOnRequirement(subsystem);
        assertThat(optionalAction.get(), equalTo(action));
    }

    @Test
    public void getActionOnSubsystem_subsystemHasNoAction_returnsEmpty() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);

        Optional<Action> optionalAction = mRequirementsControl.getActionOnRequirement(subsystem);
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

        mRequirementsControl.setDefaultActionOnSubsystem(subsystem, newAction);

        MatcherAssert.assertThat(mDefaultActionsOnSubsystems, IsMapContaining.hasEntry(subsystem, newAction));
    }

    @Test
    public void setDefaultActionOnSubsystem_defaultNotExists_putAction() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);

        Action newAction = ActionsMock.actionMocker()
                .mockWithRequirements(Collections.singleton(subsystem))
                .build();

        mRequirementsControl.setDefaultActionOnSubsystem(subsystem, newAction);

        MatcherAssert.assertThat(mDefaultActionsOnSubsystems, IsMapContaining.hasEntry(subsystem, newAction));
    }

    @Test
    public void setDefaultActionOnSubsystem_subsystemNotInRequirements_throwsIllegalArguementException() throws Exception {
        assertThrows(IllegalArgumentException.class, ()-> {
            Subsystem subsystem = mock(Subsystem.class);
            Action newAction = ActionsMock.actionMocker().build();

            mRequirementsControl.setDefaultActionOnSubsystem(subsystem, newAction);
        });
    }

    @Test
    public void getDefaultActionsToStart_noActionsAreRunning_returnsAll() throws Exception {
        Map<Subsystem, Action> defaultActions = new HashMap<>();
        defaultActions.put(mock(Subsystem.class), mock(Action.class));
        defaultActions.put(mock(Subsystem.class), mock(Action.class));

        mDefaultActionsOnSubsystems.putAll(defaultActions);

        Map<Subsystem, Action> defaultActionsToStart = mRequirementsControl.getDefaultActionsToStart();

        MatcherAssert.assertThat(defaultActionsToStart, IsMapWithSize.aMapWithSize(defaultActions.size()));
        defaultActions.forEach((s, a) -> MatcherAssert.assertThat(defaultActionsToStart, IsMapContaining.hasEntry(s, a)));
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

        Map<Subsystem, Action> defaultActionsToStart = mRequirementsControl.getDefaultActionsToStart();

        MatcherAssert.assertThat(defaultActionsToStart, IsMapWithSize.aMapWithSize(notRunning.size()));
        notRunning.forEach((s, a) -> MatcherAssert.assertThat(defaultActionsToStart, IsMapContaining.hasEntry(s, a)));
    }
}