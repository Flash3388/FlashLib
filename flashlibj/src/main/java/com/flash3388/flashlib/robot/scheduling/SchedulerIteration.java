package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
        startDefaultSubsystemActions(robotMode);

        readyForNextRun();
    }

    private void runActions(RobotMode robotMode) {
        for (Map.Entry<Action, ActionContext> entry : mActionsRepository.getRunningActionContexts()) {
            Action action = entry.getKey();
            ActionContext context = entry.getValue();

            try {
                if (robotMode.equals(RobotMode.DISABLED) &&
                        !context.runWhenDisabled()) {
                    mActionsToRemove.add(action);
                    continue;
                }

                if (!context.run()) {
                    mActionsToRemove.add(action);
                }
            } catch (Throwable t) {
                mLogger.error("Error while running an action", t);
            }
        }
    }

    private void startDefaultSubsystemActions(RobotMode robotMode) {
        for (Map.Entry<Subsystem, Action> entry : mActionsRepository.getDefaultActionsToStart().entrySet()) {
            try {
                Action action = entry.getValue();

                if (robotMode.equals(RobotMode.DISABLED) &&
                        !action.runWhenDisabled()) {
                    continue;
                }

                mLogger.debug("Starting default action for {}", entry.getKey());
                action.start();
            } catch (Throwable t) {
                mLogger.error("Error when starting default action", t);
            }
        }
    }

    private void readyForNextRun() {
        mActionsRepository.updateActionsForNextRun(mActionsToRemove);
    }
}
