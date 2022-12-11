package com.flash3388.flashlib.scheduling;

public interface ExecutionContext {

    enum ExecutionResult {
        STILL_RUNNING,
        FINISHED
    }

    ExecutionResult execute(SchedulerMode mode);

    void interrupt();
}
