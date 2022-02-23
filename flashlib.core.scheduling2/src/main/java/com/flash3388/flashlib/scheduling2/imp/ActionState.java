package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class ActionState {

    enum Step {
        PENDING,
        RUNNING,
        FINISHED
    }

    enum FinishReason {
        DONE,
        ERROR,
        INTERRUPT
    }

    private final Clock mClock;

    private Step mCurrentStep;
    private FinishReason mFinishReason;
    private Time mStartTime;
    private Throwable mError;

    public ActionState(Clock clock) {
        mClock = clock;

        mCurrentStep = Step.PENDING;
        mFinishReason = null;
        mStartTime = Time.INVALID;
        mError = null;
    }

    public Step getCurrentStep() {
        return mCurrentStep;
    }

    public FinishReason getFinishReason() {
        return mFinishReason;
    }

    public Time getStartTime() {
        return mStartTime;
    }

    public Throwable getError() {
        return mError;
    }

    public void markRunning() {
        mStartTime = mClock.currentTime();
        mCurrentStep = Step.RUNNING;
    }

    public void markDone() {
        mFinishReason = FinishReason.DONE;
    }

    public void markCanceled() {
        mFinishReason = FinishReason.INTERRUPT;
    }

    public void markErrored(Throwable t) {
        mError = t;
        mFinishReason = FinishReason.ERROR;
    }

    public void markFinished() {
        mCurrentStep = Step.FINISHED;
    }
}
