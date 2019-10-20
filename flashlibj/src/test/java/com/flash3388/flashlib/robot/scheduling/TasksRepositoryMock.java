package com.flash3388.flashlib.robot.scheduling;

import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class TasksRepositoryMock {

    private final TasksRepository mTasksRepository;
    private final Collection<SchedulerTask> mRunningTasks;

    public TasksRepositoryMock(TasksRepository tasksRepository) {
        mTasksRepository = tasksRepository;
        mRunningTasks = new ArrayList<>();

        when(mTasksRepository.getRunningTasks()).thenReturn(mRunningTasks);
        doAnswer((Answer<Void>) invocation -> {
            mRunningTasks.add(invocation.getArgument(0));
            return null;
        }).when(mTasksRepository).addTask(any(SchedulerTask.class));
    }

    public void runningTask(SchedulerTask task) {
        mRunningTasks.add(task);
    }
}
