package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.scheduling2.actions.Action;
import com.flash3388.flashlib.scheduling2.actions.ActionContext;
import com.flash3388.flashlib.scheduling2.actions.Configuration;
import com.flash3388.flashlib.scheduling2.actions.Status;
import com.flash3388.flashlib.scheduling2.actions.StatusImpl;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionsControl {

    private final RequirementsControl mRequirementsControl;
    private final Clock mClock;
    private final Logger mLogger;

    private final Collection<ActionContext> mActionsToStart;
    private final Collection<ActionContext> mRunningActions;
    private final Collection<ActionContext> mActionsToRemove;

    public ActionsControl(RequirementsControl requirementsControl, Clock clock, Logger logger) {
        mRequirementsControl = requirementsControl;
        mClock = clock;
        mLogger = logger;

        mActionsToStart = new HashSet<>(3);
        mRunningActions = new HashSet<>(5);
        mActionsToRemove = new ArrayList<>(3);
    }

    public Status addActionPending(Action action, Configuration configuration) {
        Status status = new StatusImpl(mClock.currentTime());
        ActionContext context = new ActionContext(
                action, configuration, status,
                mClock, mLogger);
        mActionsToStart.add(context);

        mLogger.debug("Starting action {}", context);

        return status;
    }

    public void runActions(SchedulerMode mode) {
        for (ActionContext context : mRunningActions) {
            if (!context.run(mode)) {
                mActionsToRemove.add(context);
            }
        }
    }

    public void startDefaultSubsystemActions() {
        for (Map.Entry<Requirement, ActionContext> entry : mRequirementsControl.getDefaultActionsToStart()
                .entrySet()) {
            ActionContext context = entry.getValue();

            mActionsToStart.add(context);
            mLogger.debug("Starting default action for {}", context);
        }
    }

    public void startNewActions() {
        for (ActionContext context : mActionsToStart) {
            boolean didConfigure = false;
            if (!context.isConfigured()) {
                context.configure();
                didConfigure = true;
            }

            Set<ActionContext> conflicts = mRequirementsControl.getConflicting(context.getRequirements());
            if (conflicts.isEmpty()) {
                mRequirementsControl.updateRequirementsTaken(context.getRequirements(), context);
                mRunningActions.add(context);

                mLogger.debug("Action configured {}", context);
            } else {
                for (ActionContext conflict : conflicts) {
                    if (didConfigure) {
                        mLogger.warn("Requirements conflict in Scheduler between new {} and old {}",
                                context, conflict);
                    }

                    conflict.cancel();
                }
            }
        }

        mActionsToStart.removeAll(mRunningActions);
    }

    public void removeFinished() {
        for (ActionContext context : mActionsToRemove) {
            mLogger.debug("Finished action {}", context);
            mRunningActions.remove(context);

            if (context.isConfigured()) {
                mRequirementsControl.updateRequirementsFree(context.getRequirements());
            }
        }

        mActionsToRemove.clear();
    }

    public void cancelAllActions() {
        for (ActionContext context : mRunningActions) {
            context.cancel();
        }
        mActionsToStart.clear();
    }
}
