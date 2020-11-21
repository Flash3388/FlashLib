package com.flash3388.flashlib.scheduling.con;

import com.flash3388.flashlib.scheduling.impl.ActionState;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicActionState implements ActionState {

    private final AtomicBoolean mIsInitialized;
    private final AtomicBoolean mIsRunning;
    private final AtomicBoolean mIsCancelled;

    public AtomicActionState() {
        mIsInitialized = new AtomicBoolean(false);
        mIsRunning = new AtomicBoolean(false);
        mIsCancelled = new AtomicBoolean(false);
    }

    @Override
    public boolean isInitialized() {
        return mIsInitialized.get();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return mIsCancelled.get();
    }

    @Override
    public boolean markStarted() {
        if(mIsRunning.compareAndSet(false, true)){
            mIsInitialized.set(false);
            mIsCancelled.set(false);

            return true;
        }

        return false;
    }

    @Override
    public boolean markInitialized() {
        return mIsInitialized.compareAndSet(false, true);
    }

    @Override
    public void markCanceled() {
        if (mIsRunning.get()) {
            mIsCancelled.set(true);
        }
    }

    @Override
    public void markFinished() {
        mIsInitialized.set(false);
        mIsRunning.set(false);
        mIsCancelled.set(false);
    }
}
