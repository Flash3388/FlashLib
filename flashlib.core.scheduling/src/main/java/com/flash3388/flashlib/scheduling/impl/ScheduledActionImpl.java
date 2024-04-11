package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;

import java.util.Objects;

public class ScheduledActionImpl extends ActionPropertyAccessorImpl implements ScheduledAction {

    private final ExecutionContext mContext;

    public ScheduledActionImpl(ExecutionContext context, ObsrActionContext obsrActionContext) {
        super(obsrActionContext);
        mContext = context;
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
    public ExecutionState getState() {
        return mContext.getState();
    }

    @Override
    public void cancel() {
        if (!getState().isRunning()) {
            throw new IllegalStateException("action is not running and cannot be cancelled");
        }

        mContext.markInterrupted();
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
