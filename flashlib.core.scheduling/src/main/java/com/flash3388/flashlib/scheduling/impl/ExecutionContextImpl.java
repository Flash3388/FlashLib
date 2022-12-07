package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.actions.ActionGroup;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

public class ExecutionContextImpl implements ExecutionContext {

    private final Clock mClock;
    private final Logger mLogger;
    private final ActionGroup mGroup;
    private final RunningActionContext mContext;

    public ExecutionContextImpl(Clock clock, Logger logger, ActionGroup group, RunningActionContext context) {
        mClock = clock;
        mLogger = logger;
        mGroup = group;
        mContext = context;

        context.markStarted(mClock.currentTime());
        mLogger.debug("ActionGroup {} started action {}", mGroup, mContext);
    }

    @Override
    public ExecutionResult execute() {
        if (mContext.iterate(mClock.currentTime())) {
            mLogger.debug("ActionGroup {} finished action {}", mGroup, mContext);
            return ExecutionResult.FINISHED;
        }

        return ExecutionResult.STILL_RUNNING;
    }

    @Override
    public void interrupt() {
        mContext.markForCancellation();
        mContext.iterate(mClock.currentTime());

        mLogger.debug("ActionGroup {} interrupted, canceling current action {}", mGroup, mContext);
    }
}
