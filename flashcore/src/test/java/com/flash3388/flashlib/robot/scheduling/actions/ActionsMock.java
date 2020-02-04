package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Requirement;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

    public static Action mockActionWithRequirement(Requirement requirement) {
        return mockActionWithRequirement(Collections.singleton(requirement));
    }

    public static Action mockActionWithRequirement(Set<Requirement> requirements) {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(requirements);

        doAnswer((Answer<Void>) invocation -> {
            Action parent = invocation.getArgument(0);
            parent.requires(requirements);
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
}
