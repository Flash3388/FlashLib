package com.flash3388.flashlib.scheduling;

public enum ActionGroupType {
    /**
     * Sequential execution of actions.
     * Executes until actions are finished.
     * Actions may share requirements, as they are executed in sequence.
     */
    SEQUENTIAL,
    /**
     * Parallel execution of actions.
     * Executes until actions are finished.
     * Actions may not share requirements, as they are executed in parallel.
     */
    PARALLEL,
    /**
     * Parallel execution of actions.
     * Executes until the first action finishes, the rest are cancelled.
     * Actions may not share requirements, as they are executed in parallel.
     */
    PARALLEL_RACE
}
