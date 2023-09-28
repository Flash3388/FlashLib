package com.flash3388.flashlib.scheduling.triggers.handlers;

import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.triggers.TriggerStateListener;

public class CancelOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final ActionInterface mAction;

    public CancelOnState(TriggerState triggerState, ActionInterface action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState) {
        if (mTriggerState == newState && mAction.isRunning()) {
            mAction.cancel();
        }
    }
}
