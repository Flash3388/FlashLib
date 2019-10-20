package com.flash3388.flashlib.robot.scheduling;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

    public static Action mockNotRunningAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(false);

        return action;
    }

    public static Action mockRunningAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(true);

        return action;
    }

    public static Action mockActionWithRequirement(Subsystem subsystem) {
        return mockActionWithRequirement(Collections.singleton(subsystem));
    }

    public static Action mockActionWithRequirement(Set<Subsystem> subsystems) {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(subsystems);

        doAnswer((Answer<Void>) invocation -> {
            Action parent = invocation.getArgument(0);
            parent.requires(subsystems);
            return null;
        }).when(action).setParent(any(Action.class));

        return action;
    }

    public static Action mockActionWithoutRequirements() {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(Collections.emptySet());

        return action;
    }
}
