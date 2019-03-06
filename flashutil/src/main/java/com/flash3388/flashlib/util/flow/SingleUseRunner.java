package com.flash3388.flashlib.util.flow;

import java.io.Closeable;

public abstract class SingleUseRunner implements Runner, Closeable {

    private final SingleUseRunnerControl mSingleUseRunnerControl;

    public SingleUseRunner() {
        mSingleUseRunnerControl = new SingleUseRunnerControl();
    }

    @Override
    public final boolean isRunning() {
        return mSingleUseRunnerControl.isRunning();
    }

    public final boolean isTerminated() {
        return mSingleUseRunnerControl.isTerminated();
    }

    @Override
    public final synchronized void start() {
        mSingleUseRunnerControl.checkCanStart();

        startRunner();

        mSingleUseRunnerControl.markStart();
    }

    @Override
    public final synchronized void stop() {
        mSingleUseRunnerControl.checkCanStop();

        stopRunner();

        mSingleUseRunnerControl.markStop();
    }

    @Override
    public final void close() {
        stop();
    }

    protected abstract void startRunner();
    protected abstract void stopRunner();
}
