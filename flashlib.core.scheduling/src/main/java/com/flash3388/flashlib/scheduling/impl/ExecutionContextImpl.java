package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

public class ExecutionContextImpl implements ExecutionContext {

    private final Clock mClock;
    private final Logger mLogger;
    private final RunningActionContext mContext;

    public ExecutionContextImpl(Clock clock, Logger logger, RunningActionContext context) {
        mClock = clock;
        mLogger = logger;
        mContext = context;

        mContext.markStarted();
        mLogger.debug("Action {} started running", mContext);
    }

    @Override
    public ExecutionResult execute(SchedulerMode mode) {
        if (mode.isDisabled() && !mContext.shouldRunInDisabled()) {
            mLogger.warn("Action {} is not allowed to run in disabled. Cancelling", mContext);
            interrupt();

            return ExecutionResult.FINISHED;
        }

        // run normally
        return execute();
    }

    @Override
    public ExecutionResult execute() {
        if (mContext.iterate()) {
            mLogger.debug("Action {} finished", mContext);
            return ExecutionResult.FINISHED;
        }

        return ExecutionResult.STILL_RUNNING;
    }

    @Override
    public void interrupt() {
        mContext.markForCancellation();
        mContext.iterate();

        mLogger.debug("Action {} interrupted", mContext);
    }
}
