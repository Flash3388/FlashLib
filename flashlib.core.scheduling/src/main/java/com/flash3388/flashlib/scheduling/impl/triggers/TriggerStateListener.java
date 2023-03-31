package com.flash3388.flashlib.scheduling.impl.triggers;

public interface TriggerStateListener {

    void onStateChange(TriggerState newState, TriggerState lastState);
}
