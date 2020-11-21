package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.ArrayList;
import java.util.Collection;

public class UserRequests {

    private final Collection<Action> mActionsToStart;
    private final Collection<Action> mActionsToCancel;

    UserRequests(Collection<Action> actionsToStart, Collection<Action> actionsToCancel) {
        mActionsToStart = actionsToStart;
        mActionsToCancel = actionsToCancel;
    }

    public UserRequests() {
        this(new ArrayList<>(2), new ArrayList<>(2));
    }

    public Collection<Action> getActionsToStart() {
        synchronized (mActionsToStart) {
            Collection<Action> copy = new ArrayList<>(mActionsToStart);
            mActionsToStart.clear();
            return copy;
        }
    }

    public Collection<Action> getActionsToCancel() {
        synchronized (mActionsToCancel) {
            Collection<Action> copy = new ArrayList<>(mActionsToCancel);
            mActionsToCancel.clear();
            return copy;
        }
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
}
