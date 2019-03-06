package com.flash3388.flashlib.util.flow;

public class SingleUseRunnerControl {

    private volatile boolean mIsRunning;
    private volatile boolean mIsTerminated;

    public SingleUseRunnerControl() {
        mIsRunning = false;
        mIsTerminated = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isTerminated() {
        return mIsTerminated;
    }

    public void checkCanStart() {
        if (isTerminated()) {
            throw new IllegalStateException("terminated");
        }
        if (isRunning()) {
            throw new IllegalStateException("already running");
        }
    }

    public void checkCanStop() {
        if (isTerminated()) {
            throw new IllegalStateException("already terminated");
        }
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }
    }

    public void markStart() {
        mIsRunning = true;
    }

    public void markStop() {
        mIsRunning = false;
        mIsTerminated = true;
    }
}
