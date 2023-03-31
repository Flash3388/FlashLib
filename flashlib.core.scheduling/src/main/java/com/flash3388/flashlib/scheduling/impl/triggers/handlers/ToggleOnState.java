package com.flash3388.flashlib.scheduling.impl.triggers.handlers;

import com.flash3388.flashlib.scheduling.ConfiguredAction;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;

public class ToggleOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final ConfiguredAction mAction;

    public ToggleOnState(TriggerState triggerState, ConfiguredAction action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState) {
        if (mTriggerState == newState) {
            if (mAction.isRunning()) {
                mAction.cancel();
            } else {
                mAction.start();
            }
        }
    }
}
