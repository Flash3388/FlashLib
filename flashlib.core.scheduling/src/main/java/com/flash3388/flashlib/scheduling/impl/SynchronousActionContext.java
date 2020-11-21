package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class SynchronousActionContext {

    private final Action mAction;
    private final Clock mClock;
    private final ActionState mActionState;

    private final ActionConfiguration mConfiguration;

    private Time mStartTime;
    private Time mTimeout;

    public SynchronousActionContext(Action action, Clock clock, ActionState actionState) {
        mAction = action;
        mClock = clock;
        mActionState = actionState;

        mConfiguration = new ActionConfiguration(action.getConfiguration());
        mStartTime = Time.INVALID;
        mTimeout = Time.INVALID;
    }

    public SynchronousActionContext(Action action, Clock clock) {
        this(action, clock, new SynchronousActionState());
    }

    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    public boolean isStarted() {
        return mActionState.isRunning();
    }

    public boolean isRunning() {
        return mActionState.isInitialized() && mActionState.isRunning();
    }

    public Time getRunTime() {
        return mClock.currentTime().sub(mStartTime);
    }

    public void startRun() {
        if (mActionState.markStarted()) {
            mStartTime = mClock.currentTime();
            mTimeout = mConfiguration.getTimeout();
        }
    }

    public boolean run() {
        if (doRun()) {
            return true;
        }

        onRunFinished();
        return false;
    }

    public void cancelAndFinish() {
        mActionState.markCanceled();
        onRunFinished();
    }

    @Override
    public String toString() {
        return mAction.toString();
    }

    private boolean doRun() {
        if(wasTimeoutReached()) {
            mActionState.markCanceled();
        }

        if(mActionState.isCanceled()) {
            return false;
        }

        if(!mActionState.isInitialized()) {
            mStartTime = mClock.currentTime();
            mAction.initialize();
            mActionState.markInitialized();
        }

        mAction.execute();

        return !mAction.isFinished();
    }

    private boolean wasTimeoutReached(){
        if (!mTimeout.isValid()) {
            return false;
        }

        return getRunTime().after(mTimeout);
    }

    private void onRunFinished() {
        try {
            if(mActionState.isInitialized()){
                mAction.end(mActionState.isCanceled());
            }
        } finally {
            mStartTime = Time.INVALID;
            mActionState.markFinished();
        }
    }
}
