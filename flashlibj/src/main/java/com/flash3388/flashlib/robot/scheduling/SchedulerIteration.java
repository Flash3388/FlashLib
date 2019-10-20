package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

class SchedulerIteration {

    private final ActionsRepository mActionsRepository;
    private final Logger mLogger;

    private final Collection<Action> mActionsToRemove;

    public SchedulerIteration(ActionsRepository actionsRepository, Logger logger) {
        mActionsRepository = actionsRepository;
        mLogger = logger;

        mActionsToRemove = new ArrayList<>(2);
    }

    public void run(RobotMode robotMode) {
        mActionsToRemove.clear();

        runActions(robotMode);
        startDefaultSubsystemActions();

        readyForNextRun();
    }

    private void runActions(RobotMode robotMode) {
        for (Action action : mActionsRepository.getRunningActions()) {
            if (robotMode.equals(RobotMode.DISABLED) &&
                    !action.runWhenDisabled()) {
                mActionsToRemove.add(action);
                continue;
            }

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
        for (Action action : mActionsRepository.getDefaultActionsToStart()) {
            action.start();
        }
    }

    private void readyForNextRun() {
        mActionsRepository.updateActionsForNextRun(mActionsToRemove);
    }
}
