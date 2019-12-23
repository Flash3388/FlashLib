package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static com.flash3388.flashlib.robot.modes.RobotModesMock.mockNonDisabledMode;
import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockFinishedActionContext;
import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockNonFinishingActionContext;
import static com.flash3388.flashlib.robot.scheduling.actions.ActionsMock.mockNotAllowedInDisabledAction;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SchedulerIterationTest {

    private ActionsRepository mActionsRepository;

    private SchedulerIteration mSchedulerIteration;

    private ActionsRepositoryMock mActionsRepositoryMock;

    @BeforeEach
    public void setup() throws Exception {
        mActionsRepository = mock(ActionsRepository.class);
        mSchedulerIteration = new SchedulerIteration(mActionsRepository, mock(Logger.class));

        mActionsRepositoryMock = new ActionsRepositoryMock(mActionsRepository);
    }

    @Test
    public void run_withActions_runsActions() throws Exception {
        ActionContext actionContext = mockNonFinishingActionContext();
        mActionsRepositoryMock.runningAction(actionContext);

        mSchedulerIteration.run(mockNonDisabledMode());

        verify(actionContext, times(1)).run();
    }

    @Test
    public void run_subsystemsWithDefaults_startsDefaultActions() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);

        mActionsRepositoryMock.setDefaultAction(subsystem, action);

        mSchedulerIteration.run(mockNonDisabledMode());

        verify(action, times(1)).start();
    }

    @Test
    public void run_actionsIsFinished_removesAction() throws Exception {
        ActionContext actionContext = mockFinishedActionContext();
        Action action = mActionsRepositoryMock.runningAction(actionContext);

        mSchedulerIteration.run(mockNonDisabledMode());

        verify(mActionsRepository, times(1)).updateActionsForNextRun(argThat(IsIterableContaining.hasItems(action)));
    }

    @Test
    public void run_disabledMode_removesActionsNotAllowedInDisabled() throws Exception {
        Action action = mockNotAllowedInDisabledAction();
        mActionsRepositoryMock.runningAction(action);

        mSchedulerIteration.run(RobotMode.DISABLED);

        verify(mActionsRepository, times(1)).updateActionsForNextRun(argThat(IsIterableContaining.hasItems(action)));
    }

    @Test
    public void run_disabledMode_doesNotRunNotAllowedInDisabled() throws Exception {
        Action action = mockNotAllowedInDisabledAction();
        ActionContext actionContext = mActionsRepositoryMock.runningAction(action);

        mSchedulerIteration.run(RobotMode.DISABLED);

        verify(actionContext, never()).run();
    }
}