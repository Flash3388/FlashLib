package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class ActionContext {

    private final Action mAction;
    private final Clock mClock;

    private Time mStartTime;
    private Time mTimeout;

    private boolean mIsInitialized;

    public ActionContext(Action action, Clock clock) {
        mAction = action;
        mClock = clock;

        mStartTime = Time.INVALID;
        mTimeout = Time.INVALID;
        mIsInitialized = false;
    }

    public void prepareForRun() {
        mAction.markStarted();

        mStartTime = mClock.currentTime();
        mTimeout = mAction.getTimeout();
        mIsInitialized = false;
    }

    public boolean run() {
        if(wasTimeoutReached()) {
            mAction.markCanceled();
        }

        if(mAction.isCanceled()) {
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
            if(mAction.isCanceled()) {
                mAction.interrupted();
            } else {
                mAction.end();
            }
        }

        mIsInitialized = false;
        mStartTime = Time.INVALID;

        mAction.removed();
    }

    public void runCanceled() {
        mAction.markCanceled();
        runFinished();
    }

    public boolean runWhenDisabled() {
        return mAction.runWhenDisabled();
    }

    boolean wasTimeoutReached(){
        if (!mStartTime.isValid() || !mTimeout.isValid()) {
            return false;
        }

        return (mClock.currentTime().sub(mStartTime)).after(mTimeout);
    }
}
