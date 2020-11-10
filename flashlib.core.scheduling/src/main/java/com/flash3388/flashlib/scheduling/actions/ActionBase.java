package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.Scheduler;

public abstract class ActionBase implements Action {

    private final Scheduler mScheduler;
    private ActionConfiguration mConfiguration;

    protected ActionBase(Scheduler scheduler, ActionConfiguration configuration) {
        mScheduler = scheduler;
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
        mScheduler.start(this);
    }

    @Override
    public final void cancel() {
        mScheduler.cancel(this);
    }

    @Override
    public final boolean isRunning() {
        return mScheduler.isRunning(this);
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
}
