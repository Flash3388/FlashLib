package com.flash3388.flashlib.scheduling.simple;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.ActionContext;
import com.flash3388.flashlib.scheduling.ActionStore;
import com.flash3388.flashlib.scheduling.RequirementsControl;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class SynchronousActionStore implements ActionStore {

    private static final ActionContext PENDING_CONTEXT = null;

    private final Clock mClock;
    private final RequirementsControl mRequirementsControl;
    private final Logger mLogger;

    private final Map<Action, ActionContext> mRunningActions;
    private final Collection<Action> mPendingActions;

    public SynchronousActionStore(Clock clock, RequirementsControl requirementsControl, Logger logger) {
        mClock = clock;
        mRequirementsControl = requirementsControl;
        mLogger = logger;

        mRunningActions = new HashMap<>();
        mPendingActions = new ArrayList<>();
    }

    @Override
    public void add(Action action) {
        if (mRunningActions.containsKey(action)) {
            throw new IllegalStateException("action already running");
        }
        if (mPendingActions.contains(action)) {
            throw new IllegalStateException("action already scheduled to run");
        }

        mPendingActions.add(action);
    }

    @Override
    public void cancel(Action action) {
        mPendingActions.remove(action);

        ActionContext context = mRunningActions.get(action);
        if (context != null) {
            context.cancelAction();
        }
    }

    @Override
    public void cancelIf(Predicate<? super Action> predicate) {
        mPendingActions.removeIf(predicate);

        for (Map.Entry<Action, ActionContext> entry : mRunningActions.entrySet()) {
            Action action = entry.getKey();
            if (predicate.test(action)) {
                entry.getValue().cancelAction();
            }
        }
    }

    @Override
    public void cancelAll() {
        mPendingActions.clear();
        for (Map.Entry<Action, ActionContext> entry : mRunningActions.entrySet()) {
            entry.getValue().cancelAction();
        }
    }

    @Override
    public Optional<ActionContext> get(Action action) {
        return Optional.ofNullable(mRunningActions.get(action));
    }

    @Override
    public Optional<Action> getActionOnRequirement(Requirement requirement) {
        return Optional.empty();
    }

    @Override
    public void setDefaultActionOnSubsystem(Subsystem subsystem, Action action) {

    }

    @Override
    public Map<Action, ActionContext> updateActionStatus(Collection<Action> stoppedActions, SchedulerMode mode) {
        for (Action action : stoppedActions) {
            if (mRunningActions.remove(action) != null) {
                mRequirementsControl.updateRequirementsNoCurrentAction(action);
            }
        }

        addToRunning(mPendingActions);
        mPendingActions.clear();

        addToRunning(mRequirementsControl.getDefaultActionsToStart());

        return Collections.unmodifiableMap(mRunningActions);
    }

    private void addToRunning(Iterable<Action> actions) {
        for (Action action : actions) {
            ActionContext context = new SynchronousContext(action, mClock, mLogger);
            context.startRun();
            mRunningActions.put(action, context);

            mRequirementsControl.updateRequirementsWithNewRunningAction(action, context);
        }
    }
}
