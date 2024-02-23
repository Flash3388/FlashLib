package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ActionControlImpl extends ActionPropertyAccessorImpl implements ActionControl {

    private final long mId;
    private final Action mAction;
    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;
    private final ObsrActionContext mObsrActionContext;
    private final Clock mClock;
    private final Logger mLogger;

    private int mExecutionContextNextNum;

    public ActionControlImpl(long id,
                             Action action,
                             ActionConfiguration configuration,
                             ActionExecutionState executionState,
                             ObsrActionContext obsrActionContext,
                             Clock clock,
                             Logger logger) {
        super(obsrActionContext);
        mId = id;

        mAction = action;
        mConfiguration = configuration;
        mExecutionState = executionState;
        mObsrActionContext = obsrActionContext;
        mClock = clock;
        mLogger = logger;

        mExecutionContextNextNum = 0;
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
    public ExecutionContext createExecutionContext(Action action) {
        long id = ++mExecutionContextNextNum;
        ActionConfiguration configuration = new ActionConfiguration(action.getConfiguration());
        StoredObject object = mObsrActionContext.getRootObject().getChild(String.valueOf(id));
        ObsrActionContext obsrActionContext = new ObsrActionContext(object, action, configuration, true);

        return new ExecutionContextImpl(
                id,
                action,
                mId,
                mAction,
                configuration,
                obsrActionContext,
                mClock,
                mLogger);
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
