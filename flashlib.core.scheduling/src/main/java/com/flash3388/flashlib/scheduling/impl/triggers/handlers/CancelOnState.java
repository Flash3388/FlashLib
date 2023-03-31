package com.flash3388.flashlib.scheduling.impl.triggers.handlers;


import com.flash3388.flashlib.scheduling.ConfiguredAction;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;

public class CancelOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final ConfiguredAction mActionInterface;

    public CancelOnState(TriggerState triggerState, ConfiguredAction action) {
        mTriggerState = triggerState;
        mActionInterface = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState) {
        if (mTriggerState == newState && mActionInterface.isRunning()) {
            mActionInterface.cancel();
        }
    }
}
