package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.ExecutionStatus;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ExecutionContextImpl implements ExecutionContext {

    private final long mId;
    private final Action mAction;
    private final long mParentId;
    private final Action mParent;
    private final Logger mLogger;

    private final ActionConfiguration mConfiguration;
    private final ActionExecutionState mExecutionState;
    private final ActionControl mControl;

    private boolean mIsInitialized;
    private boolean mIsFinished;

    public ExecutionContextImpl(long id,
                                Action action,
                                long parentId,
                                Action parent,
                                ActionConfiguration configuration,
                                ObsrActionContext obsrActionContext,
                                Clock clock,
                                Logger logger) {
        mId = id;
        mAction = action;
        mParentId = parentId;
        mParent = parent;
        mConfiguration = configuration;
        mLogger = logger;

        mExecutionState = new ActionExecutionState(mConfiguration, obsrActionContext, clock, logger);
        mControl = new ActionControlImpl(id, action, mConfiguration, mExecutionState, obsrActionContext, clock, logger);

        mIsInitialized = false;
        mIsFinished = false;
    }

    public ExecutionContextImpl(long id,
                                Action action,
                                ActionConfiguration configuration,
                                ObsrActionContext obsrActionContext,
                                Clock clock,
                                Logger logger) {
        this(id, action, -1, null, configuration, obsrActionContext, clock, logger);
    }

    @Override
    public Action getAction() {
        return mAction;
    }

    @Override
    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public ExecutionState getState() {
        ExecutionStatus status = mExecutionState.getStatus();
        switch (status) {
            case PENDING:
                return ExecutionState.pending();
            case EXECUTING: {
                Time runTime = mExecutionState.getRunTime();
                Time timeLeft = mExecutionState.getTimeLeft();
                return ExecutionState.executing(runTime, timeLeft);
            }
            case FINISHED:
            case CANCELLED:
                return ExecutionState.finished(status, mExecutionState.getFinishReason());
            default:
                throw new AssertionError("flow impossible");
        }
    }

    @Override
    public void start() {
        mExecutionState.markStarted();
        mLogger.debug("Action {} started running", this);
    }

    @Override
    public ExecutionResult execute(SchedulerMode mode) {
        verifyStarted();
        verifyNotFinished();

        if (mode != null && mode.isDisabled() && !mConfiguration.shouldRunWhenDisabled()) {
            mLogger.warn("Action {} is not allowed to run in disabled. Cancelling", this);
            interrupt();

            return ExecutionResult.FINISHED;
        }

        if (iterate()) {
            mLogger.debug("Action {} finished", this);
            mIsFinished = true;
            return ExecutionResult.FINISHED;
        }

        return ExecutionResult.STILL_RUNNING;
    }

    @Override
    public ExecutionResult execute() {
        verifyStarted();
        return execute(null);
    }

    @Override
    public void markInterrupted() {
        mExecutionState.markForCancellation();
        mLogger.debug("Action {} marked as interrupted", this);
    }

    @Override
    public void interrupt() {
        verifyNotFinished();

        markInterrupted();
        iterate();
        mIsFinished = true;

        mLogger.debug("Action {} interrupted", this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mAction.getClass().getSimpleName());
        builder.append('{');
        builder.append("scheduleId=");
        builder.append(mId);
        builder.append(", name=");
        builder.append(mConfiguration.getName());
        builder.append('}');

        if (mParent != null) {
            builder.append(" (IN GROUP ");
            builder.append(mParent.getClass().getSimpleName());
            builder.append('{');
            builder.append("scheduleId=");
            builder.append(mParentId);
            builder.append('}');
            builder.append(')');
        }

        return builder.toString();
    }

    private void verifyStarted() {
        if (!mExecutionState.isMarkedAsStarted()) {
            throw new IllegalStateException("not started");
        }
    }

    private void verifyNotFinished() {
        if (mIsFinished) {
            throw new IllegalStateException("finished");
        }
    }

    private boolean iterate() {
        if (mExecutionState.isTimedOut()) {
            mExecutionState.markTimedOut();
        }

        if (mExecutionState.isMarkedForEnd()) {
            finishAction();
            return true;
        }

        if (!mIsInitialized) {
            initializeAction();
            mIsInitialized = true;
        } else {
            executeAction();

            if (mExecutionState.isMarkedForEnd()) {
                finishAction();
                return true;
            }
        }

        if (mExecutionState.isMarkedForEnd()) {
            finishAction();
            return true;
        }

        return false;
    }

    private void initializeAction() {
        try {
            mLogger.trace("Calling initialize for {}", this);
            mAction.initialize(mControl);
        } catch (Throwable t) {
            mExecutionState.markErrored(t);
        }

        mExecutionState.markInExecution();
    }

    private void executeAction() {
        try {
            mLogger.trace("Calling execute for {}", this);
            mAction.execute(mControl);
        } catch (Throwable t) {
            mExecutionState.markErrored(t);
        }
    }

    private void finishAction() {
        mExecutionState.markInEnd();
        if (!mIsInitialized) {
            // did not initialize and as such end should not be called
            mLogger.debug("{} was marked for end before initializing, not calling end", this);
            mExecutionState.markFinishedExecution();
            return;
        }

        try {
            FinishReason finishReason = mExecutionState.getFinishReason();
            mLogger.trace("Calling end for {} with reason={}", this, finishReason);
            mAction.end(finishReason);
            mExecutionState.markFinishedExecution();
        } catch (Throwable t) {
            mLogger.warn("Error occurred in action during the end phase. Calling again.");
            mExecutionState.markErrored(t);

            // try doing it again, so we can maybe make sure end runs.
            try {
                FinishReason finishReason = mExecutionState.getFinishReason();
                mLogger.debug("Re-Calling end for {} with reason={}", this, finishReason);
                mAction.end(finishReason);
            } catch (Throwable t1) {
                mLogger.warn("Error occurred AGAIN in action during end phase." +
                        "It is likely the action has not finished it's teardown properly");
            }

            mExecutionState.markFinishedExecution();
        }
    }
}
