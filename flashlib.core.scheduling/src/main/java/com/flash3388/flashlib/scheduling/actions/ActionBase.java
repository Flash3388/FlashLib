package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Time;

import java.lang.ref.WeakReference;

public abstract class ActionBase implements Action {

    private final WeakReference<Scheduler> mScheduler;
    private ActionConfiguration mConfiguration;

    protected ActionBase(Scheduler scheduler, ActionConfiguration configuration) {
        mScheduler = new WeakReference<>(scheduler);
        mConfiguration = configuration;

        if (mConfiguration.getName() == null) {
            mConfiguration.setName(getClass().getSimpleName());
        }
    }

    protected ActionBase(Scheduler scheduler) {
        this(scheduler, new ActionConfiguration());
    }

    protected ActionBase() {
        this(GlobalDependencies.getScheduler(), new ActionConfiguration());
    }

    @Override
    public final void start() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }
        scheduler.start(this);
    }

    @Override
    public final void cancel() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }
        scheduler.cancel(this);
    }

    @Override
    public final boolean isRunning() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }
        return scheduler.isRunning(this);
    }

    @Override
    public final ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public final void setConfiguration(ActionConfiguration configuration) {
        if (isRunning()) {
            throw new IllegalStateException("Action is running, cannot change configuration");
        }

        mConfiguration = configuration;
    }

    @Override
    public final ActionConfiguration.Editor configure() {
        if (isRunning()) {
            throw new IllegalStateException("Action is running, cannot change configuration");
        }

        return new ActionConfiguration.Editor(this, getConfiguration());
    }

    @Override
    public String toString() {
        return String.format("%s{%s}", mConfiguration.getName(), getClass().getSimpleName());
    }

    public final Time getRunTime() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }
        return scheduler.getActionRunTime(this);
    }
}
