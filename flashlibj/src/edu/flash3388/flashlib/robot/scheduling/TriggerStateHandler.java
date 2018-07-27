package edu.flash3388.flashlib.robot.scheduling;

@FunctionalInterface
public interface TriggerStateHandler {
    void handleState(TriggerState state);
}
