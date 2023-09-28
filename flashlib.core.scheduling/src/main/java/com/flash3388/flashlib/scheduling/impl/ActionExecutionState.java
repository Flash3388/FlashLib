package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

public class ActionExecutionState {

    private final ObsrActionContext mObsrContext;
    private final Clock mClock;

    private Time mStartTime;
    private Time mEndTime;
    private FinishReason mFinishReason;

    private boolean mMarkedForEnd;

    public ActionExecutionState(ObsrActionContext obsrContext,
                                Clock clock) {
        mObsrContext = obsrContext;
        mClock = clock;

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

    public void updatePhase(ExecutionPhase phase) {
        mObsrContext.updatePhase(phase);
    }

    public FinishReason getFinishReason() {
        return mFinishReason;
    }

    public boolean isMarkedForEnd() {
        return mMarkedForEnd;
    }

    public void markStarted(ActionConfiguration configuration) {
        Time now = mClock.currentTime();

        mStartTime = now;
        if (configuration.getTimeout().isValid()) {
            mEndTime = now.add(configuration.getTimeout());
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

    public void markErrored() {
        mMarkedForEnd = true;
        mFinishReason = FinishReason.ERRORED;
        mObsrContext.updateStatus(ExecutionStatus.CANCELLED);
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
