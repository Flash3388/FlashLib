package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.UUID;

public class ActionControlImpl implements ActionControl {

    private final Action mAction;
    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;
    private final ObsrActionContext mObsrActionContext;
    private final Clock mClock;
    private final Logger mLogger;

    public ActionControlImpl(Action action,
                             ActionConfiguration configuration,
                             ActionExecutionState executionState,
                             ObsrActionContext obsrActionContext,
                             Clock clock,
                             Logger logger) {
        mAction = action;
        mConfiguration = configuration;
        mExecutionState = executionState;
        mObsrActionContext = obsrActionContext;
        mClock = clock;
        mLogger = logger;
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
    public StoredObject getPropertiesRoot() {
        return mObsrActionContext.getPropertiesRoot();
    }

    @Override
    public ExecutionContext createExecutionContext(Action action) {
        StoredObject object = mObsrActionContext.getRootObject().getChild(UUID.randomUUID().toString());
        RunningActionContext context = new RunningActionContext(action,
                mAction,
                new ObsrActionContext(object),
                mClock,
                mLogger);
        return new ExecutionContextImpl(mClock, mLogger, context);
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
