package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ActionsRepository {

    private final Map<Subsystem, Action> mActionsOnSubsystems;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;
    private final Map<Action, ActionContext> mRunningActions;
    private final Collection<Action> mNextRunActions;

    private final Clock mClock;
    private final Logger mLogger;

    ActionsRepository(Map<Subsystem, Action> actionsOnSubsystems, Map<Subsystem, Action> defaultActionsOnSubsystems, Map<Action, ActionContext> runningActions, Collection<Action> nextRunActions, Clock clock, Logger logger) {
        mActionsOnSubsystems = actionsOnSubsystems;
        mDefaultActionsOnSubsystems = defaultActionsOnSubsystems;
        mRunningActions = runningActions;
        mNextRunActions = nextRunActions;
        mClock = clock;
        mLogger = logger;
    }

    public ActionsRepository(Clock clock, Logger logger) {
        this(new HashMap<>(5), new HashMap<>(5), new HashMap<>(5), new ArrayList<>(2), clock, logger);
    }

    public void addAction(Action action) {
        if (mRunningActions.containsKey(action)) {
            throw new IllegalArgumentException("action already running");
        }
        if (mNextRunActions.contains(action)) {
            throw new IllegalArgumentException("action already scheduled to run");
        }

        mNextRunActions.add(action);
    }

    public Optional<Action> getActionOnSubsystem(Subsystem subsystem) {
        return Optional.ofNullable(mActionsOnSubsystems.get(subsystem));
    }

    public void setDefaultActionOnSubsystem(Subsystem subsystem, Action action) {
        if (!action.getRequirements().contains(subsystem)) {
            action.requires(subsystem);
        }

        mDefaultActionsOnSubsystems.put(subsystem, action);
    }

    public void removeAllActions() {
        mNextRunActions.clear();

        mRunningActions.forEach(this::onInternalRemove);
        mRunningActions.clear();
    }

    public void removeActionsIf(Predicate<Action> removalPredicate) {
        mNextRunActions.removeIf(removalPredicate);

        Collection<Action> actionsToRemove = mRunningActions.keySet().stream()
                .filter(removalPredicate)
                .collect(Collectors.toList());

        actionsToRemove.forEach((action) -> {
            ActionContext context = mRunningActions.get(action);
            onInternalRemove(action, context);
        });
        actionsToRemove.forEach(mRunningActions::remove);
    }

    public void updateActionsForNextRun(Iterable<Action> actionsToRemove) {
        actionsToRemove.forEach(this::internalRemove);

        mNextRunActions.forEach(this::internalAdd);
        mNextRunActions.clear();
    }

    public Set<Map.Entry<Action, ActionContext>> getRunningActionContexts() {
        return mRunningActions.entrySet();
    }

    public Map<Subsystem, Action> getDefaultActionsToStart() {
        Map<Subsystem, Action> actionsToStart = new HashMap<>();

        for (Map.Entry<Subsystem, Action> entry : mDefaultActionsOnSubsystems.entrySet()) {
            if (mActionsOnSubsystems.containsKey(entry.getKey())) {
                continue;
            }

            actionsToStart.put(entry.getKey(), entry.getValue());
        }

        return actionsToStart;
    }

    private void internalAdd(Action action) {
        if (mRunningActions.containsKey(action)) {
            return;
        }

        updateRequirementsWithNewRunningAction(action);

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
        updateRequirementsNoCurrentAction(action);
    }

    private void updateRequirementsWithNewRunningAction(Action action) {
        for (Subsystem subsystem : action.getRequirements()) {
            if (mActionsOnSubsystems.containsKey(subsystem)) {
                Action currentAction = mActionsOnSubsystems.get(subsystem);
                currentAction.cancel();

                mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over subsystem {}",
                        currentAction.toString(), action.toString(), subsystem.toString());
            }

            mActionsOnSubsystems.put(subsystem, action);
        }
    }

    private void updateRequirementsNoCurrentAction(Action action) {
        for (Subsystem subsystem : action.getRequirements()) {
            mActionsOnSubsystems.remove(subsystem);
        }
    }
}
