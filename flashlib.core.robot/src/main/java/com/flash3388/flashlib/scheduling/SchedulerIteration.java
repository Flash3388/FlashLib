package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class SchedulerIteration {

    private final ActionControl mActionControl;
    private final RequirementsControl mRequirementsControl;
    private final Logger mLogger;

    private final Collection<Action> mActionsToRemove;

    SchedulerIteration(ActionControl actionControl, RequirementsControl requirementsControl, Logger logger) {
        mActionControl = actionControl;
        mRequirementsControl = requirementsControl;
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
        for (Map.Entry<Action, ActionContext> entry : mActionControl.getRunningActionContexts()) {
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
                action.cancel();
            }
        }
    }

    private void startDefaultSubsystemActions(RobotMode robotMode) {
        for (Map.Entry<Subsystem, Action> entry : mRequirementsControl.getDefaultActionsToStart().entrySet()) {
            try {
                Action action = entry.getValue();

                if (robotMode.equals(RobotMode.DISABLED) &&
                        !action.getConfiguration().shouldRunWhenDisabled()) {
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
        mActionControl.updateActionsForNextRun(mActionsToRemove);
    }
}
