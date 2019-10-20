package com.flash3388.flashlib.robot.scheduling;

import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class ActionsRepositoryMock {

    private final ActionsRepository mActionsRepository;
    private final Collection<Action> mRunningActions;
    private final Set<Subsystem> mSubsystems;

    public ActionsRepositoryMock(ActionsRepository tasksRepository) {
        mActionsRepository = tasksRepository;
        mRunningActions = new ArrayList<>();
        mSubsystems = new HashSet<>();

        when(mActionsRepository.getRunningActions()).thenReturn(mRunningActions);
        doAnswer((Answer<Void>) invocation -> {
            mRunningActions.add(invocation.getArgument(0));
            return null;
        }).when(mActionsRepository).addAction(any(Action.class));

        when(mActionsRepository.getSubsystems()).thenReturn(mSubsystems);
    }

    public void runningAction(Action action) {
        mRunningActions.add(action);
    }

    public void registeredSubsystem(Subsystem subsystem) {
        mSubsystems.add(subsystem);
    }
}
