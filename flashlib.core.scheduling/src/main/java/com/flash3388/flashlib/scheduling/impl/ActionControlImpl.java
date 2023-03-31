package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.util.UUID;

public class ActionControlImpl implements ActionControl {

    private final ActionState mState;
    private final Clock mClock;
    private final Logger mLogger;

    public ActionControlImpl(ActionState state, Clock clock, Logger logger) {
        mState = state;
        mClock = clock;
        mLogger = logger;
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mState.getConfiguration();
    }

    @Override
    public Time getRunTime() {
        Time now = mClock.currentTime();
        return now.sub(mState.getStartTime());
    }

    @Override
    public Time getTimeLeft() {
        if (!mState.getConfiguration().getTimeout().isValid()) {
            return Time.INVALID;
        }

        Time now = mClock.currentTime();
        return mState.getEndTime().sub(now);
    }

    @Override
    public ExecutionContext newExecutionContext(ActionInterface actionInterface, ActionConfiguration configuration) {
        String id = UUID.randomUUID().toString();
        ActionState state = new ActionState(id, configuration);
        ActionContext context = new ActionContextImpl(actionInterface, state, mClock, mLogger);

        return new ExecutionContextImpl(mClock, mLogger, context);
    }

    @Override
    public void finish() {
        mLogger.trace("Action set finished from action");
        mState.markFinished();
    }

    @Override
    public void cancel() {
        mLogger.trace("Action set cancelled from action");
        mState.markCancelled(false);
    }
}
