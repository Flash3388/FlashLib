package com.flash3388.flashlib.robot.scheduling;

import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ActionsRepositoryMock {

    private final ActionsRepository mActionsRepository;
    private final Collection<Action> mRunningActions;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    public ActionsRepositoryMock(ActionsRepository tasksRepository) {
        mActionsRepository = tasksRepository;
        mRunningActions = new ArrayList<>();
        mDefaultActionsOnSubsystems = new HashMap<>();

        when(mActionsRepository.getRunningActionContexts()).thenReturn(mRunningActions);
        doAnswer((Answer<Void>) invocation -> {
            mRunningActions.add(invocation.getArgument(0));
            return null;
        }).when(mActionsRepository).addAction(any(Action.class));

        when(mActionsRepository.getDefaultActionsToStart()).thenReturn(mDefaultActionsOnSubsystems.values());
    }

    public void runningAction(Action action) {
        mRunningActions.add(action);
    }

    public void setDefaultAction(Subsystem subsystem, Action action) {
        mDefaultActionsOnSubsystems.put(subsystem, action);
    }
}
