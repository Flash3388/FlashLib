package com.flash3388.flashlib.scheduling.con;

import com.flash3388.flashlib.scheduling.ActionContext;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.simple.SynchronousContext;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;

public class StoreUpdate {

    private final Clock mClock;
    private final Logger mLogger;

    private final Map<Requirement, ActionContext> mActionsOnRequirementsStatus;
    private final Map<Action, ActionContext> mRunningActions;
    private final Map<Subsystem, Action> mDefaultActions;

    public StoreUpdate(Clock clock, Logger logger,
                       Map<Requirement, ActionContext> actionsOnRequirementsStatus,
                       Map<Action, ActionContext> runningActions, Map<Subsystem, Action> defaultActions) {
        mClock = clock;
        mLogger = logger;

        mActionsOnRequirementsStatus = actionsOnRequirementsStatus;
        mRunningActions = runningActions;
        mDefaultActions = defaultActions;
    }

    public void run(Iterable<Action> stoppedActions, SchedulerMode mode) {
        removeStoppedActions(stoppedActions);
        startNewActions();
        startDefaultActions(mode);
    }

    private void removeStoppedActions(Iterable<Action> stoppedActions) {
        for (Action action : stoppedActions) {
            Collection<Requirement> requirements = action.getConfiguration().getRequirements();
            mActionsOnRequirementsStatus.keySet().removeAll(requirements);
            mRunningActions.remove(action);
        }
    }

    private void startNewActions() {
        for (Map.Entry<Action, ActionContext> entry : mRunningActions.entrySet()) {
            Action action = entry.getKey();
            ActionContext context = entry.getValue();

            startAction(action, context, mActionsOnRequirementsStatus);
        }
    }

    private void startDefaultActions(SchedulerMode mode) {
        for (Map.Entry<Subsystem, Action> entry : mDefaultActions.entrySet()) {
            Subsystem subsystem = entry.getKey();
            Action action = entry.getValue();

            if (!mActionsOnRequirementsStatus.containsKey(subsystem) && !mode.isDisabled()) {
                ActionContext context = new SynchronousContext(action, mClock, mLogger);
                startAction(action, context, mActionsOnRequirementsStatus);
            }
        }
    }

    private void startAction(Action action, ActionContext context, Map<Requirement, ActionContext> actionsOnRequirementsStatus) {
        if (context.startRun()) {
            for (Requirement requirement : action.getConfiguration().getRequirements()) {
                ActionContext previous = actionsOnRequirementsStatus.put(requirement, context);
                if (previous != null) {
                    mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over requirement {}",
                            previous.toString(), action.toString(), requirement.toString());

                    previous.cancelAction();
                }
            }
        }
    }
}
