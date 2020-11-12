package com.flash3388.flashlib.scheduling.con;

import com.flash3388.flashlib.scheduling.ActionContext;
import com.flash3388.flashlib.scheduling.ActionStore;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.simple.SynchronousContext;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class ConcurrentActionStore implements ActionStore {
    // TODO: ISSUE WITH SYNCHRONICITY
    // When running an update, changes are made to the map copies which are not reflected in the actual maps.
    // so calling any other method is problematic.

    private final Clock mClock;
    private final Logger mLogger;

    private final Map<Action, ActionContext> mRunningActions;
    private final Map<Requirement, ActionContext> mActionsOnRequirement;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;

    public ConcurrentActionStore(Clock clock, Logger logger) {
        mClock = clock;
        mLogger = logger;

        mRunningActions = new ConcurrentHashMap<>();
        mActionsOnRequirement = new ConcurrentHashMap<>();
        mDefaultActionsOnSubsystems = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Action action) {
        ActionContext context = new SynchronousContext(action, mClock, mLogger);
        ActionContext previous = mRunningActions.putIfAbsent(action, context);
        if (previous != null) {
            throw new IllegalStateException("action already running");
        }
    }

    @Override
    public void cancel(Action action) {
        ActionContext context = mRunningActions.get(action);
        if (context != null) {
            context.cancelAction();
        }
    }

    @Override
    public void cancelIf(Predicate<? super Action> predicate) {
        for (Map.Entry<Action, ActionContext> entry : mRunningActions.entrySet()) {
            Action action = entry.getKey();
            ActionContext context = entry.getValue();

            if (predicate.test(action)) {
                context.cancelAction();
            }
        }
    }

    @Override
    public void cancelAll() {
        for (Map.Entry<Action, ActionContext> entry : mRunningActions.entrySet()) {
            ActionContext context = entry.getValue();
            context.cancelAction();
        }
    }

    @Override
    public Optional<ActionContext> get(Action action) {
        return Optional.ofNullable(mRunningActions.get(action));
    }

    @Override
    public Optional<Action> getActionOnRequirement(Requirement requirement) {
        ActionContext context = mActionsOnRequirement.get(requirement);
        if (context != null) {
            return Optional.of(context.getUnderlyingAction());
        }

        return Optional.empty();
    }

    @Override
    public void setDefaultActionOnSubsystem(Subsystem subsystem, Action action) {
        Action old = mDefaultActionsOnSubsystems.put(subsystem, action);
        if (old != null) {
            // TODO: CANCEL OLD
        }
    }

    @Override
    public Map<Action, ActionContext> updateActionStatus(Collection<Action> stoppedActions, SchedulerMode mode) {
        // TODO: FINISH
        Map<Requirement, ActionContext> actionsOnRequirementsStatus = new HashMap<>(); // from requirements control
        Map<Action, ActionContext> runningActions = new HashMap<>(); // from mRunningActions
        Map<Subsystem, Action> defaultActions = new HashMap<>(); // from requirements control

        StoreUpdate storeUpdate = new StoreUpdate(mClock, mLogger,
                actionsOnRequirementsStatus, runningActions, defaultActions);
        storeUpdate.run(stoppedActions, mode);

        // update the other maps
        mRunningActions.putAll(runningActions);

        return runningActions;
    }
}
