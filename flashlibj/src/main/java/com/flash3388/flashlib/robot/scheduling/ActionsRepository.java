package com.flash3388.flashlib.robot.scheduling;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ActionsRepository {

    private final Map<Subsystem, Action> mActionsOnSubsystems;
    private final Map<Subsystem, Action> mDefaultActionsOnSubsystems;
    private final Collection<Action> mRunningActions;
    private final Collection<Action> mNextRunActions;

    private final Logger mLogger;

    public ActionsRepository(Map<Subsystem, Action> actionsOnSubsystems, Map<Subsystem, Action> defaultActionsOnSubsystems, Collection<Action> runningActions, Collection<Action> nextRunActions, Logger logger) {
        mActionsOnSubsystems = actionsOnSubsystems;
        mDefaultActionsOnSubsystems = defaultActionsOnSubsystems;
        mRunningActions = runningActions;
        mNextRunActions = nextRunActions;
        mLogger = logger;
    }

    public ActionsRepository(Logger logger) {
        this(new HashMap<>(5), new HashMap<>(5), new ArrayList<>(5), new ArrayList<>(2), logger);
    }

    public void addAction(Action action) {
        if (mRunningActions.contains(action)) {
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

        Collection<Action> actionsToRemove = mRunningActions.stream()
                .filter(removalPredicate)
                .collect(Collectors.toList());

        actionsToRemove.forEach(this::onInternalRemove);
        mRunningActions.removeAll(actionsToRemove);
    }

    public void updateActionsForNextRun(Iterable<Action> actionsToRemove) {
        actionsToRemove.forEach(this::internalRemove);

        mNextRunActions.forEach(this::internalAdd);
        mNextRunActions.clear();
    }

    public Collection<Action> getRunningActions() {
        return mRunningActions;
    }

    public Collection<Action> getDefaultActionsToStart() {
        Collection<Action> actionsToStart = new ArrayList<>();

        for (Map.Entry<Subsystem, Action> entry : mDefaultActionsOnSubsystems.entrySet()) {
            if (mActionsOnSubsystems.containsKey(entry.getKey())) {
                continue;
            }

            actionsToStart.add(entry.getValue());
            mLogger.debug("Starting default action for {}", entry.getKey().toString());
        }

        return actionsToStart;
    }

    private void internalAdd(Action action) {
        if (mRunningActions.contains(action)) {
            return;
        }

        updateRequirementsWithNewRunningAction(action);

        mRunningActions.add(action);
    }

    private void internalRemove(Action action) {
        if (mRunningActions.remove(action)) {
            onInternalRemove(action);
        }
    }

    private void onInternalRemove(Action action) {
        action.removed();
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
