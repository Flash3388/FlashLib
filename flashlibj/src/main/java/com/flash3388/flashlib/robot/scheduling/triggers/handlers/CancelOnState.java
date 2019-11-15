package com.flash3388.flashlib.robot.scheduling.triggers.handlers;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerStateListener;

public class CancelOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final Action mAction;

    public CancelOnState(TriggerState triggerState, Action action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState) {
        if (mTriggerState == newState) {
            mAction.cancel();
        }
    }
}
