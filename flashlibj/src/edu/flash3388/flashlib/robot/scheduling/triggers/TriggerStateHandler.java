package edu.flash3388.flashlib.robot.scheduling.triggers;

@FunctionalInterface
public interface TriggerStateHandler {
    void handleState(TriggerState state);
}
