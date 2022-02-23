package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.Status;
import com.flash3388.flashlib.time.Time;

public class StatusImpl implements Status {

    private final ActionState mState;
    private final ActionContext mContext;

    public StatusImpl(ActionState state, ActionContext context) {
        mState = state;
        mContext = context;
    }

    @Override
    public boolean isPending() {
        return mState.getCurrentStep() == ActionState.Step.PENDING;
    }

    @Override
    public boolean isRunning() {
        return mState.getCurrentStep() == ActionState.Step.RUNNING;
    }

    @Override
    public boolean isDone() {
        return mState.getCurrentStep() == ActionState.Step.FINISHED;
    }

    @Override
    public boolean isSuccessful() {
        return isDone() && mState.getFinishReason() == ActionState.FinishReason.DONE;
    }

    @Override
    public boolean isErrored() {
        return isDone() && mState.getFinishReason() == ActionState.FinishReason.ERROR;
    }

    @Override
    public boolean isCanceled() {
        return isDone() && mState.getFinishReason() == ActionState.FinishReason.INTERRUPT;
    }

    @Override
    public Time getStartTime() {
        if (!isRunning()) {
            throw new IllegalStateException("Action not running");
        }

        return mState.getStartTime();
    }

    @Override
    public Throwable getError() {
        if (!isErrored()) {
            throw new IllegalStateException("Action not finished with error");
        }

        return mState.getError();
    }

    @Override
    public void cancel() {
        mContext.markForCancellation();
    }
}
