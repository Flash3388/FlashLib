package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

import java.util.Objects;

public class ScheduledActionImpl extends ActionPropertyAccessorImpl implements ScheduledAction {

    private final RunningActionContext mContext;

    public ScheduledActionImpl(RunningActionContext context, ObsrActionContext obsrActionContext) {
        super(obsrActionContext);
        mContext = context;
    }

    @Override
    public boolean isPending() {
        return mContext.getStatus() == ExecutionStatus.PENDING;
    }

    @Override
    public boolean isExecuting() {
        return mContext.getStatus() == ExecutionStatus.RUNNING;
    }

    @Override
    public boolean isRunning() {
        return isPending() || isExecuting();
    }

    @Override
    public boolean isFinished() {
        ExecutionStatus status = mContext.getStatus();
        return status == ExecutionStatus.FINISHED || status == ExecutionStatus.CANCELLED;
    }

    @Override
    public boolean hasTimeoutConfigured() {
        return mContext.getConfiguration().getTimeout().isValid();
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mContext.getConfiguration();
    }

    @Override
    public Time getRunTime() {
        if (!isExecuting()) {
            return Time.INVALID;
        }

        return mContext.getRunTime();
    }

    @Override
    public Time getTimeLeft() {
        if (!isExecuting() || !hasTimeoutConfigured()) {
            return Time.INVALID;
        }

        return mContext.getTimeLeft();
    }

    @Override
    public FinishReason getFinishReason() {
        if (!isFinished()) {
            throw new IllegalStateException("action has not finished, and does not have a finish reason");
        }

        return mContext.getFinishReason();
    }

    @Override
    public void cancel() {
        if (!isRunning()) {
            throw new IllegalStateException("action is not running and cannot be cancelled");
        }

        mContext.markForCancellation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledActionImpl that = (ScheduledActionImpl) o;
        return Objects.equals(mContext, that.mContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mContext);
    }

    @Override
    public String toString() {
        return String.format("Future for %s", mContext.toString());
    }
}
