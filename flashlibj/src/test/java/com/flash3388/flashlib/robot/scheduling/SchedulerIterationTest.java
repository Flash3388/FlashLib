package com.flash3388.flashlib.robot.scheduling;

import org.hamcrest.core.IsIterableContaining;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockFinishedAction;
import static com.flash3388.flashlib.robot.scheduling.ActionsMock.mockNonFinishingAction;
import static com.flash3388.flashlib.robot.scheduling.TasksMock.mockRepeatingTask;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SchedulerIterationTest {

    private TasksRepository mTasksRepository;
    private ActionsRepository mActionsRepository;

    private SchedulerIteration mSchedulerIteration;

    private TasksRepositoryMock mTasksRepositoryMock;
    private ActionsRepositoryMock mActionsRepositoryMock;

    @Before
    public void setup() throws Exception {
        mTasksRepository = mock(TasksRepository.class);
        mActionsRepository = mock(ActionsRepository.class);

        mSchedulerIteration = new SchedulerIteration(mTasksRepository, mActionsRepository, mock(Logger.class));

        mTasksRepositoryMock = new TasksRepositoryMock(mTasksRepository);
        mActionsRepositoryMock = new ActionsRepositoryMock(mActionsRepository);
    }

    @Test
    public void run_runModeAll_runsActionsAndTasks() throws Exception {
        Action action = mockNonFinishingAction();
        mActionsRepositoryMock.runningAction(action);

        SchedulerTask task = mockRepeatingTask();
        mTasksRepositoryMock.runningTask(task);

        mSchedulerIteration.run(SchedulerRunMode.ALL);

        verify(action, times(1)).run();
        verify(task, times(1)).run();
    }

    @Test
    public void run_runModeActions_runsActionsOnly() throws Exception {
        Action action = mockNonFinishingAction();
        mActionsRepositoryMock.runningAction(action);

        SchedulerTask task = mockRepeatingTask();
        mTasksRepositoryMock.runningTask(task);

        mSchedulerIteration.run(SchedulerRunMode.ACTIONS_ONLY);

        verify(action, times(1)).run();
        verify(task, never()).run();
    }

    @Test
    public void run_runModeTasks_runsTasksOnly() throws Exception {
        Action action = mockNonFinishingAction();
        mActionsRepositoryMock.runningAction(action);

        SchedulerTask task = mockRepeatingTask();
        mTasksRepositoryMock.runningTask(task);

        mSchedulerIteration.run(SchedulerRunMode.TASKS_ONLY);

        verify(action, never()).run();
        verify(task, times(1)).run();
    }

    @Test
    public void run_runModeDisabled_runNothing() throws Exception {
        Action action = mockNonFinishingAction();
        mActionsRepositoryMock.runningAction(action);

        SchedulerTask task = mockRepeatingTask();
        mTasksRepositoryMock.runningTask(task);

        mSchedulerIteration.run(SchedulerRunMode.DISABLED);

        verify(action, never()).run();
        verify(task, never()).run();
    }

    @Test
    public void run_subsystemsWithDefaults_startsDefaultActions() throws Exception {
        Subsystem subsystem = mock(Subsystem.class);
        Action action = mock(Action.class);

        mActionsRepositoryMock.setDefaultAction(subsystem, action);

        mSchedulerIteration.run(SchedulerRunMode.ACTIONS_ONLY);

        verify(action, times(1)).start();
    }

    @Test
    public void run_actionsIsFinished_removesAction() throws Exception {
        Action action = mockFinishedAction();
        mActionsRepositoryMock.runningAction(action);

        mSchedulerIteration.run(SchedulerRunMode.ACTIONS_ONLY);

        verify(mActionsRepository, times(1)).updateActionsForNextRun(argThat(IsIterableContaining.hasItems(action)));
    }
}