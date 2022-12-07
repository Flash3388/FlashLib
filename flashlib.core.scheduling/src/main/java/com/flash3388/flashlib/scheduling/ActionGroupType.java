package com.flash3388.flashlib.scheduling;

public enum ActionGroupType {
    /**
     * Sequential execution of actions.
     * Executes until actions are finished.
     */
    SEQUENTIAL,
    /**
     * Parallel execution of actions.
     * Executes until actions are finished.
     */
    PARALLEL,
    /**
     * Parallel execution of actions.
     * Executes until the first action finishes, the rest are cancelled.
     */
    PARALLEL_RACE
}
