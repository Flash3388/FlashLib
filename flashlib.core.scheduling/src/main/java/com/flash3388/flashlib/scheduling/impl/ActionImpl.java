package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.Action;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionGroup;
import com.flash3388.flashlib.scheduling.ActionInterface;

public class ActionImpl implements Action {

    private final ActionInterface mAction;
    private final ActionConfiguration mConfiguration;

    public ActionImpl(ActionInterface action, ActionConfiguration configuration) {
        mAction = action;
        mConfiguration = configuration;
    }

    @Override
    public void start() {
        /*
                if (mPendingActions.containsKey(action) || mRunningActions.containsKey(action)) {
            throw new IllegalArgumentException("Action already started");
        }
         */
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public ActionGroup andThen(ActionInterface... actions) {
        return null;
    }

    @Override
    public ActionGroup alongWith(ActionInterface... actions) {
        return null;
    }

    @Override
    public ActionGroup raceWith(ActionInterface... actions) {
        return null;
    }
}
