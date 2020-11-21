package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class SynchronousActionContext {

    private final Action mAction;
    private final Clock mClock;
    private final Logger mLogger;
    private final ActionState mActionState;

    private final ActionConfiguration mConfiguration;

    private Time mStartTime;
    private Time mTimeout;

    public SynchronousActionContext(Action action, Clock clock, Logger logger, ActionState actionState) {
        mAction = action;
        mClock = clock;
        mLogger = logger;
        mActionState = actionState;

        mConfiguration = new ActionConfiguration(action.getConfiguration());
        mStartTime = Time.INVALID;
        mTimeout = Time.INVALID;
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
        try {
            if (doRun()) {
                return true;
            }
        } catch (Throwable t) {
            mLogger.error(String.format("Error while running an action %s", mConfiguration.getName()), t);
            mActionState.markCanceled();
        }

        onRunFinished();
        return false;
    }

    public void cancelAndFinish() {
        mActionState.markCanceled();
        onRunFinished();
    }

    private boolean doRun() {
        if(wasTimeoutReached()) {
            mActionState.markCanceled();
        }

        if(mActionState.isCanceled()) {
            return false;
        }

        if(mActionState.markInitialized()) {
            mStartTime = mClock.currentTime();
            mAction.initialize();
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
        if(mActionState.isInitialized()){
            try {
                mAction.end(mActionState.isCanceled());
            } catch (Throwable t) {
                mLogger.error(String.format("Error while ending an action %s", mConfiguration.getName()), t);
            }
        }

        mStartTime = Time.INVALID;
        mActionState.markFinished();
    }
}
