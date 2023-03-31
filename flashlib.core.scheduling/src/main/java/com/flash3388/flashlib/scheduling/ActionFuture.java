package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

public interface ActionFuture {

    ActionConfiguration getConfiguration();
    Time getRunTime();
    Time getTimeLeft();
    ExecutionPhase getPhase();
    ExecutionState getState();
    FinishReason getFinishReason();

    void cancel();

    default boolean isRunning() {
        return getState() == ExecutionState.RUNNING;
    }

    default boolean isFinished() {
        return getState() == ExecutionState.FINISHED;
    }
}
