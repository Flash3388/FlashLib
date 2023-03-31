package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ExecutionPhase;
import com.flash3388.flashlib.scheduling.ExecutionState;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.time.Time;

public class ActionState {

    private final String mId;
    private final ActionConfiguration mConfiguration;

    private ExecutionPhase mPhase;
    private ExecutionState mState;
    private Time mStartTime;
    private Time mEndTime;
    private boolean mMarkedCancelled;
    private boolean mMarkedFinished;
    private boolean mHasTimedOut;

    public ActionState(String id, ActionConfiguration configuration) {
        mId = id;
        mConfiguration = configuration;

        mPhase = ExecutionPhase.INITIALIZATION;
        mState = ExecutionState.PENDING;
        mStartTime = Time.INVALID;
        mEndTime = Time.INVALID;
        mMarkedCancelled = false;
        mMarkedFinished = false;
        mHasTimedOut = false;
    }

    public String getId() {
        return mId;
    }

    public ActionConfiguration getConfiguration() {
        return mConfiguration;
    }

    public boolean shouldFinish() {
        return mMarkedFinished || mMarkedCancelled;
    }

    public Time getStartTime() {
        return mStartTime;
    }

    public Time getEndTime() {
        return mEndTime;
    }

    public ExecutionPhase getPhase() {
        return mPhase;
    }

    public void setPhase(ExecutionPhase phase) {
        mPhase = phase;
    }

    public ExecutionState getState() {
        return mState;
    }

    public void setState(ExecutionState state) {
        mState = state;
    }

    public void markStarted(Time now) {
        if (mState != ExecutionState.PENDING) {
            throw new IllegalStateException("cannot start if not pending");
        }

        mState = ExecutionState.RUNNING;
        mMarkedCancelled = false;
        mHasTimedOut = true;

        mStartTime = now;

        Time timeout = mConfiguration.getTimeout();
        if (timeout.isValid()) {
            mEndTime = now.add(timeout);
        } else {
            mEndTime = Time.INVALID;
        }
    }

    public void markCancelled(boolean timedOut) {
        mMarkedCancelled = true;
        mHasTimedOut = timedOut;
    }

    public void markFinished() {
        mMarkedFinished = true;
    }

    public FinishReason getFinishReason() {
        if (mMarkedCancelled) {
            if (mHasTimedOut) {
                return FinishReason.TIMEDOUT;
            } else {
                return FinishReason.CANCELLED;
            }
        }

        if (mMarkedFinished) {
            return FinishReason.FINISHED;
        }

        throw new IllegalStateException("not finished");
    }
}
