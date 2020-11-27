package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

class StsUserRequests {

    private final Collection<Action> mActionsToStart;
    private final Collection<Action> mActionsToCancel;
    private volatile SchedulerMode mCurrentMode;

    StsUserRequests(Collection<Action> actionsToStart, Collection<Action> actionsToCancel) {
        mActionsToStart = actionsToStart;
        mActionsToCancel = actionsToCancel;
        mCurrentMode = null;
    }

    public StsUserRequests() {
        this(new ArrayList<>(2), new ArrayList<>(2));
    }

    public Collection<Action> getAndClearActionsToStart() {
        synchronized (mActionsToStart) {
            Collection<Action> copy = new ArrayList<>(mActionsToStart);
            mActionsToStart.clear();
            return copy;
        }
    }

    public Collection<Action> getAndClearActionsToCancel() {
        synchronized (mActionsToCancel) {
            Collection<Action> copy = new ArrayList<>(mActionsToCancel);
            mActionsToCancel.clear();
            return copy;
        }
    }

    public boolean isCurrentModeDisable() {
        return mCurrentMode == null || mCurrentMode.isDisabled();
    }

    public void actionToStart(Action action) {
        synchronized (mActionsToStart) {
            mActionsToStart.add(action);
        }
    }

    public void actionToCancel(Action action) {
        synchronized (mActionsToCancel) {
            mActionsToCancel.add(action);
        }
    }

    public void actionsToCancel(Collection<Action> runningActions) {
        synchronized (mActionsToCancel) {
            mActionsToCancel.addAll(runningActions);
        }
    }

    public void updateCurrentMode(SchedulerMode mode) {
        mCurrentMode = mode;
    }
}
