package com.flash3388.flashlib.scheduling.impl;

public class SynchronousActionState implements ActionState {

    private boolean mIsCanceled;
    private boolean mIsRunning;
    private boolean mIsInitialized;

    public SynchronousActionState() {
        mIsRunning = false;
        mIsCanceled = false;
        mIsInitialized = false;
    }

    @Override
    public boolean isCanceled() {
        return mIsCanceled;
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public boolean isInitialized() {
        return mIsInitialized;
    }

    @Override
    public boolean markStarted() {
        if(!mIsRunning){
            mIsCanceled = false;
            mIsRunning = true;
            mIsInitialized = false;

            return true;
        }

        return false;
    }

    @Override
    public boolean markInitialized() {
        if (!mIsInitialized) {
            mIsInitialized = true;
            return true;
        }

        return false;
    }

    @Override
    public void markCanceled() {
        if (mIsRunning) {
            mIsCanceled = true;
        }
    }

    @Override
    public void markFinished() {
        mIsCanceled = false;
        mIsRunning = false;
        mIsInitialized = false;
    }
}
