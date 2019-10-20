package com.flash3388.flashlib.robot.scheduling;

import java.util.ArrayList;
import java.util.Collection;

class TasksRepository {

    private final Collection<SchedulerTask> mRunningTasks;
    private final Collection<SchedulerTask> mNextRunTasks;

    TasksRepository(Collection<SchedulerTask> runningTasks, Collection<SchedulerTask> nextRunTasks) {
        mRunningTasks = runningTasks;
        mNextRunTasks = nextRunTasks;
    }

    public TasksRepository() {
        this(new ArrayList<>(5), new ArrayList<>(2));
    }

    public void addTask(SchedulerTask task) {
        mNextRunTasks.add(task);
    }

    public void removeTask(SchedulerTask task) {
        mRunningTasks.remove(task);
        mNextRunTasks.remove(task);
    }

    public void removeAll() {
        mRunningTasks.clear();
        mNextRunTasks.clear();
    }

    public void updateTasksForNextRun(Iterable<SchedulerTask> tasksToRemove) {
        tasksToRemove.forEach(this::internalRemove);

        mNextRunTasks.forEach(this::internalAdd);
        mNextRunTasks.clear();
    }

    public Collection<SchedulerTask> getRunningTasks() {
        return mRunningTasks;
    }

    private void internalAdd(SchedulerTask task) {
        mRunningTasks.add(task);
    }

    private void internalRemove(SchedulerTask task) {
        mRunningTasks.remove(task);
    }
}
