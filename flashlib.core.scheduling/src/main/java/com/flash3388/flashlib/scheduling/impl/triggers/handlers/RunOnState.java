package com.flash3388.flashlib.scheduling.impl.triggers.handlers;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerActionController;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerState;
import com.flash3388.flashlib.scheduling.impl.triggers.TriggerStateListener;

import java.util.function.Supplier;

public class RunOnState implements TriggerStateListener {

    private final TriggerState mTriggerState;
    private final Supplier<Action> mAction;
    private Action mLastAction;

    public RunOnState(TriggerState triggerState, Supplier<Action> action) {
        mTriggerState = triggerState;
        mAction = action;
        mLastAction = null;
    }

    public RunOnState(TriggerState triggerState, Action action) {
        this(triggerState, Suppliers.of(action));
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState, TriggerActionController controller) {
        if (mTriggerState == newState) {
            mLastAction = mAction.get();
            controller.addActionToStartIfRunning(mLastAction);
        } else {
            if (mLastAction != null) {
                controller.addActionToStopIfRunning(mLastAction);
                mLastAction = null;
            }
        }
    }
}
