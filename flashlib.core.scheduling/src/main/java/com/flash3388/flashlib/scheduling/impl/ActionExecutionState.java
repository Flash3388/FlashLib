package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ActionExecutionState {

    private final ActionConfiguration mConfiguration;
    private final ObsrActionContext mObsrContext;
    private final Clock mClock;
    private final Logger mLogger;

    private Time mStartTime;
    private Time mEndTime;
    private FinishReason mFinishReason;

    private boolean mMarkedForEnd;

    public ActionExecutionState(ActionConfiguration configuration,
                                ObsrActionContext obsrContext,
                                Clock clock,
                                Logger logger) {
        mConfiguration = configuration;
        mObsrContext = obsrContext;
        mClock = clock;
        mLogger = logger;

        mStartTime = Time.INVALID;
        mEndTime = Time.INVALID;
        mFinishReason = FinishReason.FINISHED;
        mMarkedForEnd = false;
    }

    public Time getStartTime() {
        return mStartTime;
    }

    public Time getRunTime() {
        return mClock.currentTime().sub(mStartTime);
    }

    public Time getTimeLeft() {
        if (!mEndTime.isValid()) {
            return Time.INVALID;
        }

        return mClock.currentTime().sub(mEndTime);
    }

    public boolean isTimedOut() {
        if (!mEndTime.isValid()) {
            return false;
        }

        return mClock.currentTime().largerThanOrEquals(mEndTime);
    }

    public FinishReason getFinishReason() {
        return mFinishReason;
    }

    public boolean isMarkedForEnd() {
        return mMarkedForEnd;
    }

    public void updatePhase(ExecutionPhase phase) {
        mObsrContext.updatePhase(phase);
    }

    public void markStarted() {
        Time now = mClock.currentTime();

        mStartTime = now;
        if (mConfiguration.getTimeout().isValid()) {
            mEndTime = now.add(mConfiguration.getTimeout());
        } else {
            mEndTime = Time.INVALID;
        }

        mObsrContext.updateStatus(ExecutionStatus.RUNNING);
        mObsrContext.updatePhase(ExecutionPhase.INITIALIZATION);
    }

    public void markForFinish() {
        mMarkedForEnd = true;
        mFinishReason = FinishReason.FINISHED;
    }

    public void markForCancellation() {
        mMarkedForEnd = true;
        mFinishReason = FinishReason.CANCELED;
        mObsrContext.updateStatus(ExecutionStatus.CANCELLED);
    }

    public void markErrored(Throwable t) {
        mMarkedForEnd = true;
        mFinishReason = FinishReason.ERRORED;
        mObsrContext.updateStatus(ExecutionStatus.CANCELLED);

        mLogger.error("Error while running an action", t);
    }

    public void markTimedOut() {
        mMarkedForEnd = true;
        mFinishReason = FinishReason.TIMEDOUT;
        mObsrContext.updateStatus(ExecutionStatus.CANCELLED);
    }

    public void markFinishedExecution() {
        mObsrContext.updateStatus(ExecutionStatus.FINISHED);
        mObsrContext.finished();
    }
}
