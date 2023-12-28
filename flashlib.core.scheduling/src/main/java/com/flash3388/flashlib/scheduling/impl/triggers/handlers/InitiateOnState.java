package com.flash3388.flashlib.scheduling.impl.triggers.handlers;

import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionController;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;
import com.flash3388.flashlib.scheduling.statemachines.Transition;

public class InitiateOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final Transition mTransition;

    public InitiateOnState(TriggerState triggerState, Transition transition) {
        mTriggerState = triggerState;
        mTransition = transition;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState, TriggerActionController controller) {
        if (mTriggerState == newState) {
            mTransition.initiate();
        }
    }
}
