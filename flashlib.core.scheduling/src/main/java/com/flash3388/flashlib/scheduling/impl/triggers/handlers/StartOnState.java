package com.flash3388.flashlib.scheduling.impl.triggers.handlers;


import com.flash3388.flashlib.scheduling.ConfiguredAction;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;

public class StartOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final ConfiguredAction mAction;

    public StartOnState(TriggerState triggerState, ConfiguredAction action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState) {
        if (mTriggerState == newState && !mAction.isRunning()) {
            mAction.start();
        }
    }
}
