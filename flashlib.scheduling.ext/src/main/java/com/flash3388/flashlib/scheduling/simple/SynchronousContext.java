package com.flash3388.flashlib.scheduling.simple;

import com.flash3388.flashlib.scheduling.ActionState;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.ActionContext;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class SynchronousContext implements ActionContext {

    private final Action mAction;
    private final Clock mClock;
    private final Logger mLogger;
    private final ActionState mActionState;

    private final ActionConfiguration mConfiguration;

    private Time mStartTime;
    private Time mTimeout;

    public SynchronousContext(Action action, Clock clock, Logger logger, ActionState actionState) {
        mAction = action;
        mClock = clock;
        mLogger = logger;
        mActionState = actionState;

        mConfiguration = new ActionConfiguration(action.getConfiguration());
        mStartTime = Time.INVALID;
        mTimeout = Time.INVALID;
    }

    public SynchronousContext(Action action, Clock clock, Logger logger) {
        this(action, clock, logger, new SynchronousActionState());
    }

    @Override
    public Action getUnderlyingAction() {
        return mAction;
    }

    @Override
    public Time getRunTime() {
        return mClock.currentTime().sub(mStartTime);
    }

    @Override
    public boolean startRun() {
        if (mActionState.markStarted()) {
            mStartTime = mClock.currentTime();
            mTimeout = mConfiguration.getTimeout();

            return true;
        }

        return false;
    }

    @Override
    public boolean run(SchedulerMode mode) {
        // acquire
        if (mode.isDisabled() && !mConfiguration.shouldRunWhenDisabled()) {
            onRunFinished();
            return false;
        }

        try {
            if (doRun()) {
                return true;
            }
        } catch (Throwable t) {
            mLogger.error(String.format("Error while running an action %s", mConfiguration.getName()), t);
            cancelAction();
        }

        onRunFinished();
        return false;
        // release
    }

    @Override
    public void cancelAction() {
        mActionState.markCanceled();
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
