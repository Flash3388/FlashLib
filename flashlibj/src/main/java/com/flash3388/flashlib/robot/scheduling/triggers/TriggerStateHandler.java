package com.flash3388.flashlib.robot.scheduling.triggers;

@FunctionalInterface
public interface TriggerStateHandler {
    void handleStateChange(TriggerState newState, TriggerState lastState);
}
