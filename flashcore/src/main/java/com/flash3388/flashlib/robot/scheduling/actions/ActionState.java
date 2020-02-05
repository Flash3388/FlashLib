package com.flash3388.flashlib.robot.scheduling.actions;

class ActionState {

    private boolean mIsCanceled;
    private boolean mIsRunning;

    ActionState() {
        mIsRunning = false;
        mIsCanceled = false;
    }

    boolean isRunning() {
        return mIsRunning;
    }

    boolean isCanceled() {
        return mIsCanceled;
    }

    void markStarted() {
        if(!mIsRunning){
            mIsCanceled = false;
            mIsRunning = true;
        }
    }

    void markCanceled() {
        if (mIsRunning) {
            mIsCanceled = true;
        }
    }

    void finished(){
        mIsCanceled = false;
        mIsRunning = false;
    }

    void validateRunning() {
        if (!mIsRunning) {
            throw new IllegalStateException("action not running");
        }
    }

    void validateNotRunning() {
        if (mIsRunning) {
            throw new IllegalStateException("action running");
        }
    }
}
