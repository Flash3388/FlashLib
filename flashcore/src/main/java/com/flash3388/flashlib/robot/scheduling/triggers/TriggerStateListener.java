package com.flash3388.flashlib.robot.scheduling.triggers;

public interface TriggerStateListener {

    void onStateChange(TriggerState newState, TriggerState lastState);

    default void updateInState(TriggerState state) { }
}
