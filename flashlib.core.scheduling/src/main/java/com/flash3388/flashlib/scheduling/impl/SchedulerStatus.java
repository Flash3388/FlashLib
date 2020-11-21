package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SchedulerStatus {

    private final Collection<Action> mActionsFinished;
    private final Map<Action, Time> mStartTimes;

    private final Object mActionsStateLock;

    SchedulerStatus(Collection<Action> actionsFinished, Map<Action, Time> startTimes) {
        mActionsFinished = actionsFinished;
        mStartTimes = startTimes;

        mActionsStateLock = new Object();
    }

    public SchedulerStatus() {
        this(new ArrayList<>(), new HashMap<>());
    }

    public Collection<Action> getAndClearActionsFinished() {
        synchronized (mActionsStateLock) {
            Collection<Action> copy = new ArrayList<>(mActionsFinished);
            mActionsFinished.forEach(mStartTimes::remove);
            mActionsFinished.clear();
            return copy;
        }
    }

    public Time getStartTime(Action action) {
        synchronized (mActionsStateLock) {
            return mStartTimes.get(action);
        }
    }

    public void actionsFinished(Collection<Action> finished) {
        synchronized (mActionsStateLock) {
            mActionsFinished.addAll(finished);
        }
    }

    public void actionsStarted(Map<Action, Time> startTimes) {
        synchronized (mActionsStateLock) {
            mStartTimes.putAll(startTimes);
        }
    }
}
