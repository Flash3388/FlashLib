package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class ActionContext {

    private final Action mAction;
    private final Clock mClock;

    private final ActionState mActionState;

    private Time mStartTime;
    private Time mTimeout;

    private boolean mIsInitialized;

    public ActionContext(Action action, Clock clock) {
        mAction = action;
        mClock = clock;

        mActionState = new ActionState();
        mStartTime = Time.INVALID;
        mTimeout = Time.INVALID;
        mIsInitialized = false;
    }

    public void prepareForRun() {
        mActionState.markStarted();

        mStartTime = mClock.currentTime();
        mTimeout = mAction.getConfiguration().getTimeout();
        mIsInitialized = false;
    }

    public boolean run() {
        if(wasTimeoutReached()) {
            mActionState.markCanceled();
        }

        if(mActionState.isCanceled()) {
            return false;
        }

        if(!mIsInitialized) {
            mIsInitialized = true;
            mStartTime = mClock.currentTime();
            mAction.initialize();
        }

        mAction.execute();

        return !mAction.isFinished();
    }

    public void runFinished() {
        if(mIsInitialized){
            mAction.end(mActionState.isCanceled());
        }

        mIsInitialized = false;
        mStartTime = Time.INVALID;

        mActionState.finished();
    }

    public void runCanceled() {
        mActionState.markCanceled();
        runFinished();
    }

    public boolean runWhenDisabled() {
        return mAction.getConfiguration().shouldRunWhenDisabled();
    }

    boolean wasTimeoutReached(){
        if (!mStartTime.isValid() || !mTimeout.isValid()) {
            return false;
        }

        return (mClock.currentTime().sub(mStartTime)).after(mTimeout);
    }

    public void cancelAction() {
        mActionState.markCanceled();
    }
}
