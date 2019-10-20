package com.flash3388.flashlib.robot.scheduling;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

class SchedulerIteration {

    private final TasksRepository mTasksRepository;
    private final ActionsRepository mActionsRepository;
    private final Logger mLogger;

    private final Collection<SchedulerTask> mTasksToRemove;
    private final Collection<Action> mActionsToRemove;

    public SchedulerIteration(TasksRepository tasksRepository, ActionsRepository actionsRepository, Logger logger) {
        mTasksRepository = tasksRepository;
        mActionsRepository = actionsRepository;
        mLogger = logger;

        mTasksToRemove = new ArrayList<>(2);
        mActionsToRemove = new ArrayList<>(2);
    }

    public void run(SchedulerRunMode runMode) {
        mTasksToRemove.clear();
        mActionsToRemove.clear();

        if (runMode.shouldRunTasks()) {
            runTasks();
        }

        if (runMode.shouldRunActions()) {
            runActions();
            startDefaultSubsystemActions();
        }

        readyForNextRun();
    }

    private void runTasks() {
        for (SchedulerTask task : mTasksRepository.getRunningTasks()) {
            try {
                if (!task.run()) {
                    mTasksToRemove.add(task);
                }
            } catch (Throwable t) {
                mLogger.error("Error while running a task", t);
            }
        }
    }

    private void runActions() {
        for (Action action : mActionsRepository.getRunningActions()) {
            try {
                if (!action.run()) {
                    mActionsToRemove.add(action);
                }
            } catch (Throwable t) {
                mLogger.error("Error while running an action", t);
            }
        }
    }

    private void startDefaultSubsystemActions() {
        for (Subsystem subsystem : mActionsRepository.getSubsystems()) {
            if (!subsystem.hasCurrentAction() && subsystem.hasDefaultAction()) {
                mLogger.debug("Starting default action for {}", subsystem.toString());
                subsystem.startDefaultAction();
            }
        }
    }

    private void readyForNextRun() {
        mTasksRepository.updateTasksForNextRun(mTasksToRemove);
        mActionsRepository.updateActionsForNextRun(mActionsToRemove);
    }
}
