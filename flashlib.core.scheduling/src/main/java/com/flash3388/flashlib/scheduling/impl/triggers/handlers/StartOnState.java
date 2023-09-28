package com.flash3388.flashlib.scheduling.impl.triggers.handlers;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionController;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;

public class StartOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final Action mAction;

    public StartOnState(TriggerState triggerState, Action action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState, TriggerActionController controller) {
        if (mTriggerState == newState) {
            controller.addActionToStartIfRunning(mAction);
        }
    }
}
