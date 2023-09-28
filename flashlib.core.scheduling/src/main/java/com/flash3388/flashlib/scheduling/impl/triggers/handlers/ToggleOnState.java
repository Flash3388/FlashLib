package com.flash3388.flashlib.scheduling.impl.triggers.handlers;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionController;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;

public class ToggleOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final Action mAction;

    public ToggleOnState(TriggerState triggerState, Action action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState, TriggerActionController controller) {
        if (mTriggerState == newState) {
            controller.addActionToToggle(mAction);
        }
    }
}
