package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

class ActionControl {

    private final Clock mClock;
    private final RequirementsControl mRequirementsControl;

    private final Map<Action, ActionContext> mRunningActions;
    private final Collection<Action> mNextRunActions;

    ActionControl(Clock clock, RequirementsControl requirementsControl, Map<Action, ActionContext> runningActions, Collection<Action> nextRunActions) {
        mClock = clock;
        mRequirementsControl = requirementsControl;
        mRunningActions = runningActions;
        mNextRunActions = nextRunActions;
    }

    public ActionControl(Clock clock, RequirementsControl requirementsControl) {
        this(clock, requirementsControl, new HashMap<>(5), new ArrayList<>(2));
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
        ActionContext context = mRunningActions.get(action);
        if (context != null) {
            context.cancelAction();
        } else {
            throw new IllegalStateException("action is not running");
        }
    }

    public boolean isActionRunning(Action action) {
        return mRunningActions.containsKey(action);
    }

    public void updateActionsForNextRun(Iterable<Action> actionsToRemove) {
        actionsToRemove.forEach(this::internalRemove);

        mNextRunActions.forEach(this::internalAdd);
        mNextRunActions.clear();
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

    public void stopAllActions() {
        mNextRunActions.clear();

        mRunningActions.forEach(this::onInternalRemove);
        mRunningActions.clear();
    }

    private void internalAdd(Action action) {
        if (mRunningActions.containsKey(action)) {
            return;
        }

        mRequirementsControl.updateRequirementsWithNewRunningAction(action);

        ActionContext context = new ActionContext(action, mClock);
        context.prepareForRun();
        mRunningActions.put(action, context);
    }

    private void internalRemove(Action action) {
        ActionContext context = mRunningActions.remove(action);
        if (context != null) {
            onInternalRemove(action, context);
        }
    }

    private void onInternalRemove(Action action, ActionContext context) {
        context.runFinished();
        mRequirementsControl.updateRequirementsNoCurrentAction(action);
    }
}
