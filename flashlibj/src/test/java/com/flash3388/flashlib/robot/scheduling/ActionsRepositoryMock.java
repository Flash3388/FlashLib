package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionsRepositoryMock {

    private final ActionsRepository mActionsRepository;
    private final Map<Action, ActionContext> mRunningActions;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    public ActionsRepositoryMock(ActionsRepository tasksRepository) {
        mActionsRepository = tasksRepository;
        mRunningActions = new HashMap<>();
        mDefaultActionsOnSubsystems = new HashMap<>();

        when(mActionsRepository.getRunningActionContexts()).thenReturn(mRunningActions.entrySet());
        doAnswer((Answer<Void>) invocation -> {
            Action action = invocation.getArgument(0);
            mRunningActions.put(action, new ActionContext(action, mock(Clock.class)));
            return null;
        }).when(mActionsRepository).addAction(any(Action.class));

        when(mActionsRepository.getDefaultActionsToStart()).thenReturn(mDefaultActionsOnSubsystems.values());
    }

    public Action runningAction(ActionContext actionContext) {
        Action action = mock(Action.class);
        mRunningActions.put(action, actionContext);

        return action;
    }

    public ActionContext runningAction(Action action) {
        ActionContext actionContext = mock(ActionContext.class);
        mRunningActions.put(action, actionContext);

        return actionContext;
    }

    public void setDefaultAction(Subsystem subsystem, Action action) {
        mDefaultActionsOnSubsystems.put(subsystem, action);
    }
}
