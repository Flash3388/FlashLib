package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.time.Time;

import java.util.concurrent.atomic.AtomicReference;

public class ControlImpl<R> implements Control<R> {

    private enum StatusType {
        RUNNING,
        INTERRUPTED,
        FINISHED
    }

    private final Status<R> mStatus;

    private final AtomicReference<StatusType> mStatusType;
    private R mResult;

    public ControlImpl(Status<R> status) {
        mStatus = status;

        mStatusType = new AtomicReference<>(StatusType.RUNNING);
        mResult = null;
    }

    @Override
    public Time getStartTime() {
        return mStatus.getStartTime();
    }

    @Override
    public boolean wasInterrupted() {
        return mStatusType.get() == StatusType.INTERRUPTED;
    }

    @Override
    public void finished() {
        finished(null);
    }

    @Override
    public void finished(R result) {
        mResult = result;
        mStatusType.compareAndSet(StatusType.RUNNING, StatusType.FINISHED);
    }

    @Override
    public boolean isFinished() {
        return mStatusType.get() == StatusType.FINISHED;
    }

    @Override
    public void markInterrupted() {
        mStatusType.compareAndSet(StatusType.RUNNING, StatusType.INTERRUPTED);
    }

    @Override
    public R getResult() {
        return mResult;
    }
}
