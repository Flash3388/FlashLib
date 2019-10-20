package com.flash3388.flashlib.robot.scheduling;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionsMock {

    public static Action mockNonFinishingAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(true);

        return action;
    }

    public static Action mockFinishedAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(false);

        return action;
    }

    public static Action mockActionWithRequirement(Subsystem subsystem) {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(Collections.singleton(subsystem));

        return action;
    }

    public static Action mockActionWithoutRequirements() {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(Collections.emptySet());

        return action;
    }

    public static Subsystem mockSubsystemWithAction() {
        Subsystem subsystem = mock(Subsystem.class);
        when(subsystem.hasCurrentAction()).thenReturn(true);

        return subsystem;
    }

    public static Subsystem mockSubsystemWithoutAction() {
        Subsystem subsystem = mock(Subsystem.class);
        when(subsystem.hasCurrentAction()).thenReturn(false);
        when(subsystem.hasDefaultAction()).thenReturn(true);

        return subsystem;
    }
}
