package com.flash3388.flashlib.scheduling;

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

    public void run(SchedulerMode mode) {
        mActionsToRemove.clear();
        startNewActions();

        runActions(mode);
        startDefaultSubsystemActions(mode);

        readyForNextRun();
    }

    private void runActions(SchedulerMode mode) {
        for (Map.Entry<Action, ActionContext> entry : mActionControl.getRunningActionContexts()) {
            Action action = entry.getKey();
            ActionContext context = entry.getValue();

            try {
                if (mode.isDisabled() &&
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

    private void startDefaultSubsystemActions(SchedulerMode mode) {
        for (Map.Entry<Subsystem, Action> entry : mRequirementsControl.getDefaultActionsToStart().entrySet()) {
            try {
                Action action = entry.getValue();

                if (mode.isDisabled() &&
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

    private void startNewActions() {
        mActionControl.startNewActions();
    }

    private void readyForNextRun() {
        mActionControl.updateActionsForNextRun(mActionsToRemove);
    }
}
