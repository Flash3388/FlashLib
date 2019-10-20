package com.flash3388.flashlib.robot.scheduling;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ActionsRepository {

    private final Set<Subsystem> mSubsystems;
    private final Collection<Action> mRunningActions;
    private final Collection<Action> mNextRunActions;

    private final Logger mLogger;

    public ActionsRepository(Set<Subsystem> subsystems, Collection<Action> runningActions, Collection<Action> nextRunActions, Logger logger) {
        mSubsystems = subsystems;
        mRunningActions = runningActions;
        mNextRunActions = nextRunActions;
        mLogger = logger;
    }

    public ActionsRepository(Logger logger) {
        this(new HashSet<>(5), new ArrayList<>(5), new ArrayList<>(2), logger);
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

    public void registerSubsystem(Subsystem subsystem) {
        if (mSubsystems.contains(subsystem)) {
            throw new IllegalArgumentException("subsystem already registered");
        }

        mSubsystems.add(subsystem);
    }

    public void removeAllActions() {
        mNextRunActions.clear();

        mRunningActions.forEach(this::onInternalRemove);
        mRunningActions.clear();
    }

    public void updateActionsForNextRun(Iterable<Action> actionsToRemove) {
        actionsToRemove.forEach(this::internalRemove);

        mNextRunActions.forEach(this::internalAdd);
        mNextRunActions.clear();
    }

    public Collection<Action> getRunningActions() {
        return mRunningActions;
    }

    public Set<Subsystem> getSubsystems() {
        return mSubsystems;
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
            if (subsystem.hasCurrentAction()) {
                Action currentAction = subsystem.getCurrentAction();
                currentAction.cancel();

                mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over subsystem {}",
                        currentAction.toString(), action.toString(), subsystem.toString());
            }

            subsystem.setCurrentAction(action);
        }
    }

    private void updateRequirementsNoCurrentAction(Action action) {
        for (Subsystem subsystem : action.getRequirements()) {
            subsystem.setCurrentAction(null);
        }
    }
}
