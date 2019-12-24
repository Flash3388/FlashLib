package com.flash3388.flashlib.robot.scheduling.actions.nactions;

public interface ActionInterface {

    void execute();
    void end(boolean isInterrupted);

    default void initialize() { }

    default boolean isFinished() {
        return false;
    }

    default boolean iterate() {
        execute();
        return isFinished();
    }

    void start();
    void cancel();
}
