package edu.flash3388.flashlib.util.flow;

import java.io.Closeable;

public abstract class SingleUseRunner implements Runner, Closeable {

    private volatile boolean mIsRunning;
    private volatile boolean mIsTerminated;

    public SingleUseRunner() {
        mIsRunning = false;
        mIsTerminated = false;
    }

    @Override
    public final boolean isRunning() {
        return mIsRunning;
    }

    public final boolean isTerminated() {
        return mIsTerminated;
    }

    @Override
    public final synchronized void start() {
        if (isTerminated()) {
            throw new IllegalStateException("terminated");
        }
        if (isRunning()) {
            throw new IllegalStateException("already running");
        }

        startRunner();

        mIsRunning = true;
    }

    @Override
    public final synchronized void stop() {
        if (isTerminated()) {
            throw new IllegalStateException("already terminated");
        }
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }

        stopRunner();

        mIsTerminated = true;
        mIsRunning = false;
    }

    @Override
    public final void close() {
        stop();
    }

    protected abstract void startRunner();
    protected abstract void stopRunner();
}
