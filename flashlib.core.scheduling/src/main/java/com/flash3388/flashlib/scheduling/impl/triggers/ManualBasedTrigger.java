package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.triggers.ManualTrigger;

public class ManualBasedTrigger extends TriggerBaseImpl implements GenericTrigger, ManualTrigger {

    private TriggerState mNewTriggerState;

    public ManualBasedTrigger() {
        mNewTriggerState = TriggerState.INACTIVE;
    }

    @Override
    public void update(TriggerActionController controller) {
        setState(mNewTriggerState, controller);
    }

    @Override
    public void activate() {
        mNewTriggerState = TriggerState.ACTIVE;
    }

    @Override
    public void deactivate() {
        mNewTriggerState = TriggerState.INACTIVE;
    }

    @Override
    public boolean isActive() {
        return mNewTriggerState == TriggerState.ACTIVE;
    }
}
