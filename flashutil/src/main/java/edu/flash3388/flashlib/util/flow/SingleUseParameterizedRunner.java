package edu.flash3388.flashlib.util.flow;

import java.io.Closeable;

public abstract class SingleUseParameterizedRunner<T> implements ParameterizedRunner<T>, Closeable {

    private final SingleUseRunnerControl mSingleUseRunnerControl;

    protected SingleUseParameterizedRunner() {
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
    public final synchronized void start(T param) {
        mSingleUseRunnerControl.checkCanStart();

        startRunner(param);

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

    protected abstract void startRunner(T param);
    protected abstract void stopRunner();
}
