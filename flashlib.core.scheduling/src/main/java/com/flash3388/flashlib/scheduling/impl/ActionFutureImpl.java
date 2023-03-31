package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFuture;
import com.flash3388.flashlib.scheduling.ExecutionPhase;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ActionFutureImpl implements ActionFuture {

    private final ActionConfiguration mConfiguration;
    private final ActionContext mContext;
    private final ActionState mState;
    private final Clock mClock;
    private final Logger mLogger;

    public ActionFutureImpl(ActionConfiguration configuration,
                            ActionContext context,
                            ActionState state,
                            Clock clock,
                            Logger logger) {
        mConfiguration = configuration;
        mContext = context;
        mState = state;
        mClock = clock;
        mLogger = logger;
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public Time getRunTime() {
        verifyRunning();

        Time now = mClock.currentTime();
        return now.sub(mState.getStartTime());
    }

    @Override
    public Time getTimeLeft() {
        verifyRunning();

        if (!mState.getConfiguration().getTimeout().isValid()) {
            return Time.INVALID;
        }

        Time now = mClock.currentTime();
        return mState.getEndTime().sub(now);
    }

    @Override
    public ExecutionPhase getPhase() {
        return mState.getPhase();
    }

    @Override
    public ExecutionState getState() {
        return mState.getState();
    }

    @Override
    public FinishReason getFinishReason() {
        verifyFinished();

        return mState.getFinishReason();
    }

    @Override
    public void cancel() {
        verifyRunning();

        mLogger.trace("Action set cancelled from user");
        mContext.markCancelled();
    }

    private void verifyRunning() {
        if (!isRunning()) {
            throw new IllegalStateException("action not running");
        }
    }

    private void verifyFinished() {
        if (!isFinished()) {
            throw new IllegalStateException("action not finished");
        }
    }
}
