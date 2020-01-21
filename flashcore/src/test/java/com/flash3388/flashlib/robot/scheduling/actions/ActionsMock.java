package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Subsystem;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ActionsMock {

    private ActionsMock() {
    }

    public static ActionContext mockNonFinishingActionContext() {
        ActionContext actionContext = mock(ActionContext.class);
        when(actionContext.run()).thenReturn(true);

        return actionContext;
    }

    public static ActionContext mockFinishedActionContext() {
        ActionContext actionContext = mock(ActionContext.class);
        when(actionContext.run()).thenReturn(false);

        return actionContext;
    }

    public static Action mockNotAllowedInDisabledAction() {
        Action action = mock(Action.class);
        when(action.runWhenDisabled()).thenReturn(false);

        return action;
    }

    public static Action mockNotRunningAction() {
        Action action = mock(Action.class);
        when(action.isRunning()).thenReturn(false);

        return action;
    }

    public static Action mockRunningAction() {
        Action action = mock(Action.class);
        when(action.isRunning()).thenReturn(true);

        return action;
    }

    public static Action mockActionIsFinishedMarkedTrue() {
        Action action = mock(Action.class);
        when(action.isFinished()).thenReturn(true);

        return action;
    }

    public static Action mockActionIsFinishedMarkedFalse() {
        Action action = mock(Action.class);
        when(action.isFinished()).thenReturn(false);

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

    public static Action makeActionCancelable(Action action) {
        doAnswer((Answer<Void>) invocation -> {
            when(action.isCanceled()).thenReturn(true);
            return null;
        }).when(action).markCanceled();

        return action;
    }

    public static Action mockThrowingAction() {
        Action action = mock(Action.class);
        doThrow(new RuntimeException()).when(action).execute();

        return action;
    }
}
