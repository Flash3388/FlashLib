package com.flash3388.flashlib.robot.scheduling.actions;

public abstract class ActionBase implements Action {

    private ActionConfiguration mConfiguration;

    protected ActionBase(ActionConfiguration configuration) {
        mConfiguration = configuration;
    }

    protected ActionBase() {
        this(new ActionConfiguration());
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
}
