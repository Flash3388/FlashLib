package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Time;

import java.lang.ref.WeakReference;
import java.util.Objects;

public abstract class ActionBase implements ActionInterface {

    private final WeakReference<Scheduler> mScheduler;
    private ActionConfiguration mConfiguration;

    protected ActionBase(Scheduler scheduler, ActionConfiguration configuration) {
        Objects.requireNonNull(scheduler, "scheduler null");
        Objects.requireNonNull(configuration, "configuration null");

        mScheduler = new WeakReference<>(scheduler);
        setConfiguration(configuration);
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

        if (mConfiguration.getName() == null || mConfiguration.getName().isEmpty()) {
            mConfiguration.setName(String.valueOf(hashCode()));
        }
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
        return String.format("%s{name=%s}",
                getClass().getSimpleName().isEmpty() ?
                        getClass().getName() :
                        getClass().getSimpleName(),
                mConfiguration.getName());
    }

    public final Time getRunTime() {
        Scheduler scheduler = mScheduler.get();
        if (scheduler == null) {
            throw new IllegalStateException("scheduler was garbage collected");
        }
        return scheduler.getActionRunTime(this);
    }

    @Override
    public final ActionInterface requires(Requirement... requirements) {
        return ActionInterface.super.requires(requirements);
    }

    @Override
    public final ActionInterface withTimeout(Time timeout) {
        return ActionInterface.super.withTimeout(timeout);
    }

    @Override
    public final ActionInterface flags(ActionFlag... flags) {
        return ActionInterface.super.flags(flags);
    }
}
