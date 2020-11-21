package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.ArrayList;
import java.util.Collection;

public class SchedulerStatus {

    private final Collection<Action> mActionsFinished;

    SchedulerStatus(Collection<Action> actionsFinished) {
        mActionsFinished = actionsFinished;
    }

    public SchedulerStatus() {
        mActionsFinished = new ArrayList<>();
    }

    public Collection<Action> getActionsFinished() {
        synchronized (mActionsFinished) {
            Collection<Action> copy = new ArrayList<>(mActionsFinished);
            mActionsFinished.clear();
            return copy;
        }
    }

    public void actionsFinished(Collection<Action> finished) {
        synchronized (mActionsFinished) {
            mActionsFinished.addAll(finished);
        }
    }
}
