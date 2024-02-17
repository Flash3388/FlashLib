package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ScheduledAction;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

import java.util.Objects;

public class ScheduledActionImpl implements ScheduledAction {

    private final RunningActionContext mContext;
    private final ObsrActionContext mObsrActionContext;

    public ScheduledActionImpl(RunningActionContext context) {
        mContext = context;
        mObsrActionContext = context.getObsrContext();
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
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getBoolean(defaultValue);
    }

    @Override
    public int getIntProperty(String name, int defaultValue) {
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getInt(defaultValue);
    }

    @Override
    public long getLongProperty(String name, long defaultValue) {
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getLong(defaultValue);
    }

    @Override
    public double getDoubleProperty(String name, double defaultValue) {
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getDouble(defaultValue);
    }

    @Override
    public String getStringProperty(String name, String defaultValue) {
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getString(defaultValue);
    }

    @Override
    public void putBooleanProperty(String name, boolean value) {
        mObsrActionContext.getPropertiesRoot().getEntry(name).setBoolean(value);
    }

    @Override
    public void putIntProperty(String name, int value) {
        mObsrActionContext.getPropertiesRoot().getEntry(name).setInt(value);
    }

    @Override
    public void putLongProperty(String name, long value) {
        mObsrActionContext.getPropertiesRoot().getEntry(name).setLong(value);
    }

    @Override
    public void putDoubleProperty(String name, double value) {
        mObsrActionContext.getPropertiesRoot().getEntry(name).setDouble(value);
    }

    @Override
    public void putStringProperty(String name, String value) {
        mObsrActionContext.getPropertiesRoot().getEntry(name).setString(value);
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
