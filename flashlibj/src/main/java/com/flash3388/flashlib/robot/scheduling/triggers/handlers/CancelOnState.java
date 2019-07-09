package com.flash3388.flashlib.robot.scheduling.triggers.handlers;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerStateHandler;

public class CancelOnState implements TriggerStateHandler {

    private final TriggerState mTriggerState;
    private final Action mAction;

    public CancelOnState(TriggerState triggerState, Action action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void handleStateChange(TriggerState newState, TriggerState lastState) {
        if (mTriggerState == newState) {
            mAction.cancel();
        }
    }
}
