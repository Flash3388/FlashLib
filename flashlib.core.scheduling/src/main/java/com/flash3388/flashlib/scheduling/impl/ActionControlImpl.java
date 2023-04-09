package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

public class ActionControlImpl implements ActionControl {

    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;

    public ActionControlImpl(ActionConfiguration configuration, ActionExecutionState executionState) {
        mConfiguration = configuration;
        mExecutionState = executionState;
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public Time getRunTime() {
        return mExecutionState.getRunTime();
    }

    @Override
    public Time getTimeLeft() {
        return mExecutionState.getTimeLeft();
    }

    @Override
    public void finish() {
        mExecutionState.markForFinish();
    }

    @Override
    public void cancel() {
        mExecutionState.markForCancellation();
    }
}
