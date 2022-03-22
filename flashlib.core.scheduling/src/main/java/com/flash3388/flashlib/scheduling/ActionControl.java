package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

class ActionControl {

    private final Clock mClock;
    private final RequirementsControl mRequirementsControl;
    private final Logger mLogger;

    private final Map<Action, ActionContext> mRunningActions;
    private final Collection<Action> mNextRunActions;

    ActionControl(Clock clock, RequirementsControl requirementsControl, Logger logger,
                  Map<Action, ActionContext> runningActions, Collection<Action> nextRunActions) {
        mClock = clock;
        mLogger = logger;
        mRequirementsControl = requirementsControl;
        mRunningActions = runningActions;
        mNextRunActions = nextRunActions;
    }

    public ActionControl(Clock clock, RequirementsControl requirementsControl, Logger logger) {
        this(clock, requirementsControl, logger, new HashMap<>(5), new ArrayList<>(2));
    }

    public Set<Map.Entry<Action, ActionContext>> getRunningActionContexts() {
        return mRunningActions.entrySet();
    }

    public void startAction(Action action) {
        if (mRunningActions.containsKey(action)) {
            throw new IllegalStateException("action already running");
        }
        if (mNextRunActions.contains(action)) {
            throw new IllegalStateException("action already scheduled to run");
        }

        mNextRunActions.add(action);
    }

    public void cancelAction(Action action) {
        ActionContext context = mRunningActions.remove(action);
        if (context != null) {
            context.markCanceled();
            onInternalRemove(action, context);
        } else if (!mNextRunActions.remove(action)) {
            throw new IllegalStateException("action is not running");
        }
    }

    public boolean isActionRunning(Action action) {
        return mRunningActions.containsKey(action);
    }

    public Time getActionRunTime(Action action) {
        ActionContext context = mRunningActions.get(action);
        if (context != null) {
            return context.getRunTime();
        } else {
            throw new IllegalStateException("action is not running");
        }
    }

    public void startNewActions() {
        mNextRunActions.forEach(this::internalAdd);
        mNextRunActions.clear();
    }

    public void updateActionsForNextRun(Iterable<Action> actionsToRemove) {
        actionsToRemove.forEach(this::internalRemove);
    }

    public void cancelActionsIf(Predicate<? super Action> predicate) {
        mNextRunActions.removeIf(predicate);

        Collection<Action> toRemove = new ArrayList<>();
        for (Map.Entry<Action, ActionContext> entry : mRunningActions.entrySet()) {
            if (predicate.test(entry.getKey())) {
                onInternalRemove(entry.getKey(), entry.getValue());
                toRemove.add(entry.getKey());
            }
        }

        toRemove.forEach(mRunningActions::remove);
    }

    public void cancelAllActions() {
        mNextRunActions.clear();

        mRunningActions.forEach(this::onInternalRemove);
        mRunningActions.clear();
    }

    private void internalAdd(Action action) {
        if (mRunningActions.containsKey(action)) {
            mLogger.debug("Attempted to start action {} when already running", action);
            return;
        }

        try {
            Set<Action> conflictingActions = mRequirementsControl.updateRequirementsWithNewRunningAction(action);
            conflictingActions.forEach((conflictingAction)-> {
                ActionContext context = mRunningActions.remove(conflictingAction);
                if (context != null) {
                    context.markCanceled();
                    onInternalRemove(conflictingAction, context, false);
                }
            });
        } catch (ActionHasPreferredException e) {
            // so not starting action
            mLogger.warn("Not starting action {}", action);
            return;
        }

        ActionContext context = new ActionContext(action, mClock);
        context.prepareForRun();
        mRunningActions.put(action, context);

        mLogger.debug("Started action {}", action);
    }

    private void internalRemove(Action action) {
        ActionContext context = mRunningActions.remove(action);
        if (context != null) {
            onInternalRemove(action, context);
        }
    }

    private void onInternalRemove(Action action, ActionContext context) {
        onInternalRemove(action, context, true);
    }

    private void onInternalRemove(Action action, ActionContext context, boolean updateRequirements) {
        context.runFinished();
        if (updateRequirements) {
            mRequirementsControl.updateRequirementsNoCurrentAction(action);
        }
        mLogger.debug("Finished action {}", action);
    }
}
