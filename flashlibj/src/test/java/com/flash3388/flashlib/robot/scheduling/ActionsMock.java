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
}
