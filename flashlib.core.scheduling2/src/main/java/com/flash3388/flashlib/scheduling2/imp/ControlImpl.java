package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.Control;
import com.flash3388.flashlib.time.Time;

public class ControlImpl implements Control {

    private final ActionState mState;
    private final ActionContext mContext;

    public ControlImpl(ActionState state, ActionContext context) {
        mState = state;
        mContext = context;
    }

    @Override
    public Time getStartTime() {
        if (mState.getCurrentStep() != ActionState.Step.RUNNING) {
            throw new IllegalStateException("Action not running");
        }

        return mState.getStartTime();
    }

    @Override
    public boolean wasInterrupted() {
        return mState.getFinishReason() == ActionState.FinishReason.INTERRUPT;
    }

    @Override
    public void finished() {
        mContext.markForFinish();
    }
}
