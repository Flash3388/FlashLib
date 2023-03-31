package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;


public class ExecutionContextImpl implements ExecutionContext {

    private final Clock mClock;
    private final Logger mLogger;
    private final ActionContext mContext;

    public ExecutionContextImpl(Clock clock, Logger logger, ActionContext context) {
        mClock = clock;
        mLogger = logger;
        mContext = context;

        mContext.markStarted();
        mLogger.debug("Action {} started running", mContext);
    }

    @Override
    public ExecutionResult execute() {
        mContext.execute();

        if (mContext.isFinished()) {
            mLogger.debug("Action {} finished", mContext);
            return ExecutionResult.FINISHED;
        }

        return ExecutionResult.STILL_RUNNING;
    }

    @Override
    public void interrupt() {
        mContext.markCancelled();
        mContext.execute();

        mLogger.debug("Action {} interrupted", mContext);
    }
}
