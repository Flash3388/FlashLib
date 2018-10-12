package edu.flash3388.flashlib.robot.scheduling;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SchedulerTest {

    @Test
    public void run_runModeAll_runsActionsAndTasks() throws Exception {
        Action action = mockNonFinishingAction();
        Scheduler.Task task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.ALL);
        scheduler.run();

        verify(action, times(1)).run();
        verify(task, times(1)).run();
    }

    @Test
    public void run_runModeActions_runsActionsOnly() throws Exception {
        Action action = mockNonFinishingAction();
        Scheduler.Task task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.ACTIONS_ONLY);
        scheduler.run();

        verify(action, times(1)).run();
        verify(task, times(0)).run();
    }

    @Test
    public void run_runModeTasks_runsTasksOnly() throws Exception {
        Action action = mockNonFinishingAction();
        Scheduler.Task task = mockRepeatingTask();

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.singleton(action), Collections.singleton(task), SchedulerRunMode.TASKS_ONLY);
        scheduler.run();

        verify(action, times(0)).run();
        verify(task, times(1)).run();
    }

    @Test
    public void run_runModeTasks_runNothing() throws Exception {
        Action action = mockNonFinishingAction();
        Scheduler.Task task = mockRepeatingTask();

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
    public void add_actionsWithSameRequirements_cancelsThoseActions() throws Exception {
        Subsystem subsystem = mockSubsystemWithAction();
        Action actionWithRequirement = mockActionWithRequirement(subsystem);

        Collection<Action> actionCollection = new ArrayList<>();
        actionCollection.add(actionWithRequirement);

        Scheduler scheduler = new Scheduler(Collections.emptySet(), actionCollection, Collections.emptySet(), SchedulerRunMode.ALL);

        Action actionWithSameRequirement = mockActionWithRequirement(subsystem);

        scheduler.add(actionWithSameRequirement);

        verify(subsystem, times(1)).cancelCurrentAction();
        assertThat(actionCollection, hasItem(actionWithSameRequirement));
    }

    @Test
    public void remove_taskOfRunnable_removesCorrectTask() throws Exception {
        Runnable runnable = mock(Runnable.class);
        Scheduler.Task task = new Scheduler.Task(runnable, false);

        Collection<Scheduler.Task> taskCollection = new ArrayList<>();
        taskCollection.add(task);

        Scheduler scheduler = new Scheduler(Collections.emptySet(), Collections.emptySet(), taskCollection, SchedulerRunMode.ALL);
        scheduler.remove(runnable);

        assertThat(taskCollection, empty());
    }

    private Action mockNonFinishingAction() {
        Action action = mock(Action.class);
        when(action.run()).thenReturn(true);

        return action;
    }

    private Action mockActionWithRequirement(Subsystem subsystem) {
        Action action = mock(Action.class);
        when(action.getRequirements()).thenReturn(Collections.singleton(subsystem));

        return action;
    }

    private Scheduler.Task mockRepeatingTask() {
        Scheduler.Task task = mock(Scheduler.Task.class);
        when(task.isRepeating()).thenReturn(true);

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