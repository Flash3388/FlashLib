package com.flash3388.flashlib.scheduling2.actions;

import com.flash3388.flashlib.scheduling2.ActionsControl;
import com.flash3388.flashlib.time.Time;

import java.util.concurrent.atomic.AtomicReference;

public class ControlImpl implements Control {

    private enum StatusType {
        RUNNING,
        INTERRUPTED,
        FINISHED
    }

    private final ActionsControl mActionsControl;
    private final Status mStatus;

    private final AtomicReference<StatusType> mStatusType;

    public ControlImpl(ActionsControl actionsControl, Status status) {
        mActionsControl = actionsControl;
        mStatus = status;

        mStatusType = new AtomicReference<>(StatusType.RUNNING);
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
        mStatusType.compareAndSet(StatusType.RUNNING, StatusType.FINISHED);
    }

    @Override
    public ActionExecutionBuilder start(Action action) {
        return new ActionExecutionBuilderImpl(mActionsControl, action);
    }

    @Override
    public boolean isFinished() {
        return mStatusType.get() == StatusType.FINISHED;
    }

    @Override
    public void markInterrupted() {
        mStatusType.compareAndSet(StatusType.RUNNING, StatusType.INTERRUPTED);
    }
}
