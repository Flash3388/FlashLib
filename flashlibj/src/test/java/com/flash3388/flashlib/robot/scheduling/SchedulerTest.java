package com.flash3388.flashlib.robot.scheduling;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SchedulerTest {

    @Test
    public void run_runModeAll_runsActionsAndTasks() throws Exception {
        Action action = mockNonFinishingAction();
        SchedulerTask task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.ALL);
        scheduler.run();

        verify(action, times(1)).run();
        verify(task, times(1)).run();
    }

    @Test
    public void run_runModeActions_runsActionsOnly() throws Exception {
        Action action = mockNonFinishingAction();
        SchedulerTask task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.ACTIONS_ONLY);
        scheduler.run();

        verify(action, times(1)).run();
        verify(task, times(0)).run();
    }

    @Test
    public void run_runModeTasks_runsTasksOnly() throws Exception {
        Action action = mockNonFinishingAction();
        SchedulerTask task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.TASKS_ONLY);
        scheduler.run();

        verify(action, times(0)).run();
        verify(task, times(1)).run();
    }

    @Test
    public void run_runModeTasks_runNothing() throws Exception {
        Action action = mockNonFinishingAction();
        SchedulerTask task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.DISABLED);
        scheduler.run();

        verify(action, times(0)).run();
        verify(task, times(0)).run();
    }

    @Test
    public void run_subsystemsWithDefaults_startsDefaultActions() throws Exception {
        Subsystem subsystem = mockSubsystemWithoutAction();

        Scheduler scheduler = new Scheduler(Collections.singleton(subsystem), new ArrayList<Action>(), Collections.emptySet(), SchedulerRunMode.ALL);
        scheduler.run();

        verify(subsystem, times(1)).startDefaultAction();
    }

    @Test
    public void run_actionsIsFinished_removesAction() throws Exception {
        Action action = mockFinishedAction();

        Collection<Action> actionCollection = new ArrayList<>();
        actionCollection.add(action);

        Scheduler scheduler = new Scheduler(Collections.emptySet(), actionCollection, Collections.emptySet(), SchedulerRunMode.ALL);
        scheduler.run();

        verify(action, times(1)).removed();
        assertThat(actionCollection, hasSize(0));
    }

    @Test
    public void add_actionsWithSameRequirements_cancelsTheseActions() throws Exception {
        Subsystem subsystem = mockSubsystemWithAction();
        Action actionWithRequirement = mockActionWithRequirement(subsystem);
        when(subsystem.getCurrentAction()).thenReturn(actionWithRequirement);

        Collection<Action> actionCollection = new ArrayList<>();
        actionCollection.add(actionWithRequirement);

        Scheduler scheduler = new Scheduler(Collections.emptySet(), actionCollection, Collections.emptySet(), SchedulerRunMode.ALL);

        Action actionWithSameRequirement = mockActionWithRequirement(subsystem);

        scheduler.add(actionWithSameRequirement);

        verify(actionWithRequirement, times(1)).cancel();
        assertThat(actionCollection, hasItem(actionWithSameRequirement));
    }

    @Test
    public void remove_taskOfRunnable_removesCorrectTask() throws Exception {
        Runnable runnable = mock(Runnable.class);
        SchedulerTask task = Tasks.once(runnable);

        Collection<SchedulerTask> taskCollection = new ArrayList<>();
        taskCollection.add(task);

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.emptySet(), taskCollection, SchedulerRunMode.ALL);
        scheduler.remove(task);

        assertThat(taskCollection, empty());
    }

    private Action mockNonFinishingAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(true);

        return action;
    }

    private Action mockFinishedAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(false);

        return action;
    }

    private Action mockActionWithRequirement(Subsystem subsystem) {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(Collections.singleton(subsystem));

        return action;
    }

    private SchedulerTask mockRepeatingTask() {
        SchedulerTask task = mock(SchedulerTask.class);
        when(task.run()).thenReturn(true);

        return task;
    }

    private Subsystem mockSubsystemWithAction() {
        Subsystem subsystem = mock(Subsystem.class);
        when(subsystem.hasCurrentAction()).thenReturn(true);

        return subsystem;
    }

    private Subsystem mockSubsystemWithoutAction() {
        Subsystem subsystem = mock(Subsystem.class);
        when(subsystem.hasCurrentAction()).thenReturn(false);

        return subsystem;
    }
}