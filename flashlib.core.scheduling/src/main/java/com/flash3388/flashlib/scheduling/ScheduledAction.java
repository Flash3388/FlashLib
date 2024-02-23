package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionConfiguration;
import com.flash3388.flashlib.time.Time;

/**
 * Represents an action scheduled for execution by the scheduler.
 * Allows retrieving information or controlling the execution of the action.
 * <p>
 * This instance is not reusable between different scheduling of the action. It represents a specific
 * scheduling request resulted from {@link Scheduler#start(Action)}.
 * <p>
 * Properties defined during the execution of this action will generally not remain after this action
 * has finished execution.
 *
 * @since FlashLib 3.3.0
 */
public interface ScheduledAction extends ActionPropertyAccessor { //TODO: UPDATE SINCE TO RIGHT VERSION

    /**
     * Gets a snapshot of the configuration used during the scheduling of the action.
     * This is the configuration of the action as it was during {@link Scheduler#start(Action)}.
     * It is unmodifiable.
     *
     * @return snapshot of the configuration.
     */
    ActionConfiguration getConfiguration();

    /**
     * Gets if the action has a timeout configured.
     *
     * @return <b>true</b> if timeout is configured, <b>false</b> otherwise
     * @see ActionConfiguration#getTimeout()
     */
    boolean hasTimeoutConfigured();

    /**
     * Retrieves the current {@link ExecutionState} of the action. This indicates the
     * state of execution of the action and provides several details as to the running or finishing
     * status of the action.
     *
     * @return current state
     */
    ExecutionState getState();

    /**
     * Requests the scheduler to cancel the instance's execution.
     * It is not guaranteed that the action will stop immediately, as it depends
     * both on the implementation of the scheduler and the current execution state.
     *
     * @throws IllegalStateException if the action is not running
     * @see Scheduler#cancel(Action)
     */
    void cancel();
}
