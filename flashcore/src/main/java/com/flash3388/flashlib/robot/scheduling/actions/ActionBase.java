package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Requirement;
import com.flash3388.flashlib.robot.scheduling.Scheduler;

public abstract class ActionBase implements Action {

    private final Scheduler mScheduler;
    private ActionConfiguration mConfiguration;

    protected ActionBase(Scheduler scheduler, ActionConfiguration configuration) {
        mScheduler = scheduler;
        mConfiguration = configuration;
    }

    protected ActionBase(Scheduler scheduler) {
        this(scheduler, new ActionConfiguration());
    }

    protected ActionBase() {
        this(RunningRobot.getInstance().getScheduler(), new ActionConfiguration());
    }

    @Override
    public void start() {
        mScheduler.start(this);
    }

    @Override
    public void cancel() {
        mScheduler.cancel(this);
    }

    @Override
    public boolean isRunning() {
        return mScheduler.isRunning(this);
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public void setConfiguration(ActionConfiguration configuration) {
        if (isRunning()) {
            throw new IllegalStateException("Action is running, cannot change configuration");
        }

        mConfiguration = configuration;
    }

    @Override
    public ActionConfiguration.Editor configure() {
        return new ActionConfiguration.Editor(this, getConfiguration());
    }

    @Override
    public Action requires(Requirement... requirements) {
        configure().requires(requirements).save();
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getClass().getSimpleName(), mConfiguration.getName());
    }
}
