package com.flash3388.flashlib.robot.scheduling;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class TasksRepositoryTest {

    private Collection<SchedulerTask> mRunningTasks;
    private Collection<SchedulerTask> mNextRunTasks;

    private TasksRepository mTasksRepository;

    @Before
    public void setup() throws Exception {
        mRunningTasks = new ArrayList<>();
        mNextRunTasks = new ArrayList<>();

        mTasksRepository = new TasksRepository(mRunningTasks, mNextRunTasks);
    }

    @Test
    public void addTask_forSomeTask_addsTaskForFutureRun() throws Exception {
        SchedulerTask task = mock(SchedulerTask.class);

        mTasksRepository.addTask(task);

        assertThat(mNextRunTasks, hasItems(task));
    }

    @Test
    public void removeTask_tasksIsRunning_removes() throws Exception {
        SchedulerTask task = mock(SchedulerTask.class);
        mRunningTasks.add(task);

        mTasksRepository.removeTask(task);

        assertThat(mRunningTasks, not(hasItems(task)));
    }

    @Test
    public void removeTask_tasksIsMarkedForNextRun_removes() throws Exception {
        SchedulerTask task = mock(SchedulerTask.class);
        mNextRunTasks.add(task);

        mTasksRepository.removeTask(task);

        assertThat(mNextRunTasks, not(hasItems(task)));
    }

    @Test
    public void updateTasksForNextRun_withTasksScheduledForNextRun_startsThoseTasks() throws Exception {
        SchedulerTask[] tasks = {
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class)
        };
        mNextRunTasks.addAll(Arrays.asList(tasks));

        mTasksRepository.updateTasksForNextRun(Collections.emptyList());

        assertThat(mRunningTasks, hasItems(tasks));
        assertThat(mNextRunTasks, emptyIterable());
    }

    @Test
    public void updateTasksForNextRun_withTasksToRemove_removesThoseTasksKeepsTheRest() throws Exception {
        SchedulerTask[] removeTasks = {
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class)
        };
        SchedulerTask[] keepTasks = {
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class),
                mock(SchedulerTask.class)
        };
        mRunningTasks.addAll(Arrays.asList(removeTasks));
        mRunningTasks.addAll(Arrays.asList(keepTasks));

        mTasksRepository.updateTasksForNextRun(Arrays.asList(removeTasks));

        assertThat(mRunningTasks, hasItems(keepTasks));
        assertThat(mRunningTasks, not(hasItems(removeTasks)));
    }
}